package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set

/**
 * A [Pool] which uses a [PoolDocument] to populate its properties.
 *
 * @property document the underlying [PoolDocument].
 * @property user the currently authenticated user.
 * @property store the current [Store].
 */
class PoolDocumentPool(
    private val document: PoolDocument,
    private val user: AuthenticatedUser,
    private val store: Store,
) : Pool {

  override val name: String = document.name ?: ""

  override val remainingRounds: Int = document.remainingBestOfCount ?: 0
  override val totalRounds: Int = document.tournamentBestOf ?: 0

  override val isStartNextRoundEnabled: Boolean =
      user.uid == document.tournamentAdminId && remainingRounds > 0

  override val players: List<Pool.Player>
    get() {
      val ids = document.playerIds ?: emptyList()
      val names = document.playerNames ?: emptyList()
      return ids.zip(names).map { (id, name) -> Pool.Player(uid = id, name = name) }
    }

  override suspend fun startNextRound() {
    runCatching {
      store.transaction {
        val currentPoolUid = document.uid ?: ""
        val ref = store.collection(PoolDocument.Collection).document(currentPoolUid)
        val currentPool = get<PoolDocument>(ref) ?: return@transaction
        val currentRemainingBestOfCount = currentPool.remainingBestOfCount ?: return@transaction
        val currentPlayers = currentPool.playerIds ?: emptyList()

        if (currentRemainingBestOfCount <= 0) return@transaction

        set(ref, document.copy(remainingBestOfCount = currentRemainingBestOfCount - 1))

        for (p1 in currentPlayers.indices) {
          for (p2 in currentPlayers.indices) {
            if (p1 > p2) {
              val first = if (remainingRounds % 2 == 0) p1 else p2
              val second = if (remainingRounds % 2 == 0) p2 else p1
              val match =
                  ChessDocument(
                      whiteId = currentPlayers[first],
                      blackId = currentPlayers[second],
                      lastUpdatedAt = System.currentTimeMillis(),
                      poolId = document.uid,
                      tournamentId = document.tournamentId,
                  )
              set(store.collection(ChessDocument.Collection).document(), match)
            }
          }
        }
      }
    }
  }
}
