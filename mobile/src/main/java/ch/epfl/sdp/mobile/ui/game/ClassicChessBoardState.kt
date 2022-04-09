package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.*

/**
 * An interface which represents the state of a [ChessBoard] composable. It display the current
 * state of the game, and offers some ways for the [ChessBoard] to interact with it and indicate the
 * user gestures.
 *
 * Each [ClassicChessBoardState.Piece] has unique [ClassicChessBoardState.Piece], which is used to
 * display some smooth animations if multiple pieces with the same ranks and colors exist.
 *
 * @param Piece the type of the [ClassicChessBoardState.Piece].
 */
@Stable
interface ClassicChessBoardState :
    ChessBoardState<ClassicRank, ClassicColor, ClassicChessBoardState.Piece> {

  @Stable
  interface Piece : ChessBoardState.Piece<ClassicRank, ClassicColor> {

    /** The rank of the piece. */
    override val rank: ClassicRank

    /** The color of the piece. */
    override val color: ClassicColor
  }

  /** Returns the position currently selected by the user, if there's any. */
  val selectedPosition: ChessBoardState.Position?

  /**
   * A [Set] of the positions which are available to the player for actions, depending on the pieces
   * which they are currently holding.
   */
  val availableMoves: Set<ChessBoardState.Position>

  /**
   * A callback which should be called when the given [Piece] was moved from a start position to an
   * end position.
   *
   * @param piece the [Piece] that was moved.
   * @param endPosition the place where the [Piece] was dropped.
   */
  fun onDropPiece(piece: Piece, endPosition: ChessBoardState.Position)

  /**
   * A callback which will be called when the [ChessBoard] is clicked at the given position. A
   * clicked [ChessBoard] might indicate that the user wants to play a piece.
   *
   * @param position the place that was clicked on the board.
   */
  fun onPositionClick(position: ChessBoardState.Position)
}
