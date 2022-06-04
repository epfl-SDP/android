package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.engine.Game
import kotlinx.coroutines.flow.Flow

/** Represents a [Game] between two online players. */
interface Match {

  /** The id of the [Match]. */
  val id: String?

  /** The [Flow] of the current [Game] state. */
  val game: Flow<Game>

  /** The [Flow] of the [Profile] of the white player. */
  val white: Flow<Profile?>

  /** The [Flow] of the [Profile] of the black player. */
  val black: Flow<Profile?>

  /**
   * Updates the [Match] with a new [Game].
   *
   * @param game the new [Game] with which to update the [Match].
   */
  suspend fun update(game: Game)
}
