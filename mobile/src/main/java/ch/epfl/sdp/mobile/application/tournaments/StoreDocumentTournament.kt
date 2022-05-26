package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.*
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.Collection
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.StagePools
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.stageDirectElimination
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.NotStarted
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.Pools
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import ch.epfl.sdp.mobile.infrastructure.time.TimeProvider
import ch.epfl.sdp.mobile.ui.i18n.English.tournamentDetailsPoolName
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * An implementation of a [Tournament] which uses a [TournamentDocument] under-the-hood.
 *
 * @param document the backing [TournamentDocument].
 * @param user the currently logged-in [AuthenticatedUser].
 * @param store the [Store] which can be used by the [StoreDocumentTournament].
 * @param timeProvider the [TimeProvider] used to calculate the duration of creation of the
 * tournament.
 */
class StoreDocumentTournament(
    private val document: TournamentDocument,
    private val user: AuthenticatedUser,
    private val store: Store,
    private val timeProvider: TimeProvider,
) : Tournament {
  override val reference = TournamentReference(document.uid ?: "")
  override val name = document.name ?: ""
  override val isAdmin = document.adminId == user.uid
  override val isParticipant = document.playerIds?.contains(user.uid) ?: false
  override val durationCreated = duration(document.creationTimeEpochMillis, timeProvider)

  override val status: Tournament.Status
    get() {
      val enoughParticipants = document.playerIds?.size ?: 0 >= (document.maxPlayers ?: 0)
      val stageAsRound = document.stage?.toIntOrNull()
      val eliminationRounds = document.eliminationRounds ?: 1
      return when {
        document.stage == null -> NotStarted(enoughParticipants)
        document.stage == StagePools -> Pools
        stageAsRound != null -> {
          val moveToNextRoundBannerIndex = stageAsRound.takeIf { it != 1 }
          Tournament.Status.DirectElimination(
              List(eliminationRounds - stageAsRound + 1) { index ->
                val round = eliminationRounds - index
                val pow = 2.0.pow(round - 1).toInt()
                Tournament.Status.Round("1 / $pow", round, round == moveToNextRoundBannerIndex)
              },
          )
        }
        else -> Tournament.Status.Unknown
      }
    }

  override suspend fun start() {
    runCatching {
      store.transaction {
        val tournamentDocumentReference = store.collection(Collection).document(reference.uid)
        val current = get<TournamentDocument>(tournamentDocumentReference) ?: return@transaction
        val adminId = current.adminId ?: return@transaction
        val maxPlayers = current.maxPlayers ?: return@transaction
        val poolSize = current.poolSize ?: return@transaction
        val players = current.playerIds ?: return@transaction
        val bestOf = current.bestOf ?: return@transaction
        val stage = current.stage

        // Are we the tournament admin ?
        if (adminId != user.uid) return@transaction
        if (stage != null) return@transaction

        // Poolsize of 1 or less mean no match is possible in pool phase
        if (poolSize <= 1) {
          createFinalsMatchesForPlayers(players) { it.eliminationRounds }
        } else {
          createPools(
              tournamentDocumentReference = tournamentDocumentReference,
              current = current,
              maxPlayers = maxPlayers,
              poolSize = poolSize,
              bestOf = bestOf,
              adminId = adminId,
              players = players,
          )
        }
      }
    }
  }

  override suspend fun startDirectElimination() {
    runCatching {
      val results =
          store
              .collection(ChessDocument.Collection)
              .whereEquals(ChessDocument.TournamentId, reference.uid)
              .whereNotEquals(ChessDocument.PoolId, null)
              .get<ChessDocument>()
              .toPoolResults()
      store.transaction { createMatchesForPoolResults(results) { it.eliminationRounds } }
    }
  }

  override suspend fun startNextRound() {
    runCatching {
      val round = document.stage?.toIntOrNull() ?: 1
      val results =
          store
              .collection(ChessDocument.Collection)
              .whereEquals(ChessDocument.TournamentId, reference.uid)
              .whereEquals(ChessDocument.RoundDepth, round)
              .get<ChessDocument>()
              .toPoolResults()
      store.transaction { createMatchesForPoolResults(results) { (round - 1).takeIf { it >= 1 } } }
    }
  }

  /**
   * Creates the necessary [PoolDocument]s in the [Store] for the given parameters.
   *
   * @receiver the ongoing [Transaction].
   * @param tournamentDocumentReference the reference to the tournament's [DocumentReference]
   * @param current the [TournamentDocument]
   * @param maxPlayers the maximum number of players than can join this tournament.
   * @param poolSize the target size of each pool. The number of pools derives from this number and
   * the total number of players.
   * @param bestOf the number of "best-of" rounds for the pool phase and the direct elimination
   * phase.
   * @param adminId the unique identifier of the user administrating the tournament.
   * @param players the [List] of unique identifier of users to try to place in the pools.
   */
  private fun Transaction<DocumentReference>.createPools(
      tournamentDocumentReference: DocumentReference,
      current: TournamentDocument,
      maxPlayers: Int,
      poolSize: Int,
      bestOf: Int,
      adminId: String,
      players: List<String>
  ) {
    val chosenPlayers = players.shuffled().take(maxPlayers)
    val pools =
        chosenPlayers.chunked(poolSize).map { ids ->
          ids.map { uid ->
            uid to
                (get<ProfileDocument>(store.collection(ProfileDocument.Collection).document(uid))
                    ?.name
                    ?: "")
          }
        }
    val minOpponentsPerPool = pools.lastOrNull()?.size ?: 0

    set(tournamentDocumentReference, current.copy(stage = StagePools))

    pools
        .asSequence()
        .mapIndexed { index, participants ->
          PoolDocument(
              name = tournamentDetailsPoolName(index + 1),
              tournamentId = reference.uid,
              minOpponentsForAnyPool = minOpponentsPerPool,
              remainingBestOfCount = bestOf,
              tournamentBestOf = bestOf,
              tournamentAdminId = adminId,
              playerIds = participants.map { it.first },
              playerNames = participants.map { it.second },
          )
        }
        .forEach { set(store.collection(PoolDocument.Collection).document(), it) }
  }

  /**
   * Creates the matches corresponding to the given finals phase.
   *
   * @receiver the ongoing [Transaction].
   * @param players the identifiers of the players,
   * @param nextDepth the function which computes the depth of the elimination tree.
   */
  private fun Transaction<DocumentReference>.createFinalsMatchesForPlayers(
      players: List<String>,
      nextDepth: (TournamentDocument) -> Int?,
  ) {
    val ref = store.collection(Collection).document(reference.uid)
    val currentDocument = get<TournamentDocument>(ref) ?: return

    val bestOf = currentDocument.bestOf ?: return
    val depth = nextDepth(currentDocument) ?: return
    val count = 2.0.pow(depth).toInt()
    val matches = players.take(count).chunked(2).filter { it.size == 2 }

    set(
        reference = ref,
        value = currentDocument.copy(stage = stageDirectElimination(depth)),
    )

    for (match in matches) {
      repeat(bestOf) { index ->
        val (first, second) = if (index % 2 == 0) match[0] to match[1] else match[1] to match[0]
        val matchRef = store.collection(ChessDocument.Collection).document()
        val matchDocument =
            ChessDocument(
                whiteId = first,
                blackId = second,
                lastUpdatedAt = System.currentTimeMillis(),
                poolId = null,
                roundDepth = depth,
                tournamentId = document.uid,
            )
        set(matchRef, matchDocument)
      }
    }
  }
  /**
   * Creates the matches corresponding to the given [PoolResults].
   *
   * @receiver the ongoing [Transaction].
   * @param results the [PoolResults] for which the matches are created.
   * @param nextDepth the function which computes the depth of the elimination tree.
   */
  private fun Transaction<DocumentReference>.createMatchesForPoolResults(
      results: PoolResults,
      nextDepth: (TournamentDocument) -> Int?,
  ) {
    val ranked =
        results
            .players
            .map {
              val score = results.score(it).toFloat()
              val total = results.played(it).toFloat()
              it to (if (total == 0f) 0f else score / total)
            }
            .sortedByDescending { (_, score) -> score }
            .map { it.first }

    createFinalsMatchesForPlayers(ranked, nextDepth)
  }

  /**
   * Obtains the elapsed duration from the [startTime] using the given [TimeProvider].
   *
   * @param startTime creation time in milliseconds to obtain the elapsed rounded duration.
   * @param timeProvider the [TimeProvider] used to calculate the duration of creation of the
   * tournament.
   */
  private fun duration(startTime: Long?, timeProvider: TimeProvider): Duration {
    return if (startTime != null) (timeProvider.now() - startTime).milliseconds.absoluteValue
    else 0.milliseconds
  }
}
