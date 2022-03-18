package ch.epfl.sdp.mobile.application.chess.moves

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position

/**
 * An interface representing a relative view of the current [Board], and the history of moves, which
 * can be used by pieces to indicate how they should act and perform their actions.
 */
interface GameWithRoles : Board<Piece<Role>> {

  /** The [Position] for which we're asking for some [Moves]. */
  val position: Position
}
