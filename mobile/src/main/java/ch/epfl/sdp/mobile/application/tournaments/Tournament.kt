import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo.*
import java.time.LocalDateTime

/**
 * Represents a [Tournament] in the application, containing internal information on its
 * customization.
 */
interface Tournament {

  /** The unique identifier of the tournament. */
  val uid: String

  /** The user-readable name of the tournament. */
  val name: String

  /** The creator of the tournament's unique identifier. */
  val adminId: String

  // TODO: Add Creation Date and Status attributes.

  /** The maximal number of players specified for this tournament. */
  val maxPlayers: Int

  /** The number of "best-of" rounds for the pool phase and the direct elimination phase. */
  val bestOf: Int

  /** The target size of each pool. */
  val poolSize: Int

  /** The number of direct elimination rounds of the tournament. */
  val eliminationRounds: Int

  /** The List of unique identifiers of the players of the tournament. */
  val playerIds: List<String>
}
