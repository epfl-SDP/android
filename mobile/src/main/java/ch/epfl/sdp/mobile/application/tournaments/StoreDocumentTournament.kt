package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.*
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.Collection
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.StagePools
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.stageDirectElimination
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.NotStarted
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.Pools
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import ch.epfl.sdp.mobile.ui.i18n.English.tournamentDetailsPoolName
import kotlin.math.pow

/**
 * An implementation of a [Tournament] which uses a [TournamentDocument] under-the-hood.
 *
 * @param document the backing [TournamentDocument].
 * @param user the currently logged-in [AuthenticatedUser].
 * @param store the [Store] which can be used by the [StoreDocumentTournament].
 */
class StoreDocumentTournament(
    private val document: TournamentDocument,
    private val user: AuthenticatedUser,
    private val store: Store,
) : Tournament {
  override val reference = TournamentReference(document.uid ?: "")
  override val name = document.name ?: ""
  override val isAdmin = document.adminId == user.uid
  override val isParticipant = document.playerIds?.contains(user.uid) ?: false

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
              .collection("games")
              .whereEquals("tournamentId", reference.uid)
              .whereNotEquals("poolId", null)
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
              .collection("games")
              .whereEquals("tournamentId", reference.uid)
              .whereEquals("roundDepth", round)
              .get<ChessDocument>()
              .toPoolResults()
      store.transaction { createMatchesForPoolResults(results) { (round - 1).takeIf { it >= 1 } } }
    }
  }

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
            uid to (get<ProfileDocument>(store.collection("users").document(uid))?.name ?: "")
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
        val matchRef = store.collection("games").document()
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

  private fun Transaction<DocumentReference>.createMatchesForPoolResults(
      results: PoolResults,
      nextDepth: (TournamentDocument) -> Int?,
  ) {
    val ranked =
        results.players
            .map {
              val score = results.score(it).toFloat()
              val total = results.played(it).toFloat()
              it to (if (total == 0f) 0f else score / total)
            }
            .sortedByDescending { (_, score) -> score }
            .map { it.first }

    createFinalsMatchesForPlayers(ranked, nextDepth)
  }
}
