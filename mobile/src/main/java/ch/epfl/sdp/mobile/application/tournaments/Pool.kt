package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store

/** A [Pool] represents a group of players in the qualification phase of a tournament. */
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

  /** True iff the player may start the next round for this pool. */
  val isStartNextRoundEnabled: Boolean

  /** The number of rounds which have not been played yet. */
  val remainingRounds: Int

  /** The total number of rounds to be played in the pool. */
  val totalRounds: Int

  /** Starts the next round, if there are still some remaining rounds to perform in this [Pool]. */
  suspend fun startNextRound()
}

/**
 * Transforms this [PoolDocument] into a [Pool].
 *
 * @param user the currently authenticated user.
 * @param store the current [Store].
 *
 * @return the newly created [Pool].
 */
fun PoolDocument.toPool(
    user: AuthenticatedUser,
    store: Store,
): Pool = PoolDocumentPool(this, user, store)
