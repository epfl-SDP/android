package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.Collection
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.StagePools
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.stageDirectElimination
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.NotStarted
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.Pools
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
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

  // TODO : Refine the tournament rules.
  override val status: Tournament.Status
    get() {
      val enoughParticipants = document.playerIds?.size ?: 0 >= (document.maxPlayers ?: 0)
      val stageAsRound = document.stage?.toIntOrNull()
      val eliminationRounds = document.eliminationRounds ?: 1
      return when {
        document.stage == null -> NotStarted(enoughParticipants)
        document.stage == StagePools -> Pools
        stageAsRound != null ->
            Tournament.Status.DirectElimination(
                List(eliminationRounds - stageAsRound + 1) { index ->
                  val round = eliminationRounds - index
                  val pow = 2.0.pow(round - 1).toInt()
                  Tournament.Status.Round("1 / $pow", round)
                })
        else -> Tournament.Status.Unknown
      }
    }

  override suspend fun start(): Boolean =
      runCatching {
        store.transaction {
          val tournamentDocumentReference = store.collection(Collection).document(reference.uid)
          val current =
              get<TournamentDocument>(tournamentDocumentReference) ?: return@transaction false
          val adminId = current.adminId ?: return@transaction false
          val stage = current.stage
          val maxPlayers = current.maxPlayers ?: return@transaction false
          val poolSize = current.poolSize ?: return@transaction false
          val players = current.playerIds ?: return@transaction false
          val bestOf = current.bestOf ?: return@transaction false

          // Are we the tournament admin ?
          if (adminId != user.uid) return@transaction false
          if (stage != null) return@transaction false

          val chosenPlayers = players.shuffled().take(maxPlayers)
          val pools =
              chosenPlayers.chunked(poolSize).map { ids ->
                ids.map { uid ->
                  uid to (get<ProfileDocument>(store.collection("users").document(uid))?.name ?: "")
                }
              }
          val minOpponentsPerPool = pools.lastOrNull()?.size ?: 0

          // TODO : Handle zero pool size.

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

          true
        }
      }
          .getOrElse { false }

  override suspend fun startDirectElimination() {
    runCatching {
      val results =
          store
              .collection("games")
              .whereEquals("tournamentId", reference.uid)
              .whereNotEquals("poolId", null)
              .get<ChessDocument>()
              .toPoolResults()

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

      store.transaction {
        val ref = store.collection(Collection).document(reference.uid)
        val currentDocument = get<TournamentDocument>(ref) ?: return@transaction

        val bestOf = currentDocument.bestOf ?: return@transaction
        val depth = currentDocument.eliminationRounds ?: return@transaction
        val count = 2.0.pow(depth).toInt()
        val matches = ranked.take(count).chunked(2)

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
    }
  }
}
