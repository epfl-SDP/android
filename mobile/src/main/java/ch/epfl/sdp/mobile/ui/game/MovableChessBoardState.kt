package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * An interface that extend the interface [ChessBoardState] and offers some ways for the
 * [ChessBoard] to interact with it and indicate the user gestures.
 *
 * @param Piece the type of the [ChessBoardState.Piece].
 */
@Stable
interface MovableChessBoardState<Piece : ChessBoardState.Piece> : ChessBoardState<Piece> {
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
