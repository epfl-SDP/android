package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store

interface Pool {

  /**
   * A class representing [Player] within the pool.
   *
   * @property uid the unique identifier of the player.
   * @param name the name of the player.
   */
  data class Player(val uid: String, val name: String)

  /** The name of the pool. */
  val name: String

  /** The [List] of [Player] in this pool. */
  val players: List<Player>

  val isStartNextRoundEnabled: Boolean

  val remainingRounds: Int

  val totalRounds: Int

  /** Starts the next round, if there are still some remaining rounds to perform in this [Pool]. */
  suspend fun startNextRound()
}

fun PoolDocument.toPool(
    user: AuthenticatedUser,
    store: Store,
): Pool = PoolDocumentPool(this, user, store)
