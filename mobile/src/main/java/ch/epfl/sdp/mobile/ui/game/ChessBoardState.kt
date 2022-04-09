package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

interface ChessBoardState<Rank, Color, Piece : ChessBoardState.Piece<Rank, Color>> {
  /**
   * A position for a piece on a [ChessBoard]. The top-left position has coordinates (0, 0), and the
   * x axis increases towards the right while the y axis increases towards the bottom.
   *
   * @param x the first coordinate of this position.
   * @param y the second coordinate of this position.
   */
  data class Position(val x: Int, val y: Int)

  /** An interface representing a [Piece] that may be displayed on a [ChessBoard]. */
  @Stable
  interface Piece<Rank, Color> {

    /** The rank of the piece. */
    val rank: Rank

    /** The color of the piece. */
    val color: Color
  }

  /** A [Map] of the [Piece], associated to their [Position] on the board. */
  val pieces: Map<Position, Piece>

  /** Returns the position of the [Rank.King] currently in check, if there's any. */
  val checkPosition: Position?
}
