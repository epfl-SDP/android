package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.Collection
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.StagePools
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set

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
  override val status: Tournament.Status =
      Tournament.Status.NotStarted(
          enoughParticipants = (document.playerIds?.size ?: 0 >= (document.maxPlayers ?: 0)),
      )

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
                    name = "Pool #${index + 1}",
                    tournamentId = reference.uid,
                    minOpponentsForAnyPool = minOpponentsPerPool,
                    remainingBestOfCount = bestOf,
                    playerIds = participants.map { it.first },
                    playerNames = participants.map { it.second },
                )
              }
              .forEach { set(store.collection(PoolDocument.Collection).document(), it) }

          true
        }
      }
          .getOrElse { false }
}
