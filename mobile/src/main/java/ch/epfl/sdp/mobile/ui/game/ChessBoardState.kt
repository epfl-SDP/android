package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * An interface which represents the state of a [ChessBoard] composable. It display the current
 * state of the game
 *
 * Each [ChessBoardState.Piece] has unique [ChessBoardState.Piece], which is used to display some
 * smooth animations if multiple pieces with the same ranks and colors exist.
 *
 * @param Piece the type of the [ChessBoardState.Piece].
 */
interface ChessBoardState<out Piece : ChessBoardState.Piece> {

  enum class Rank {
    King,
    Queen,
    Rook,
    Bishop,
    Knight,
    Pawn,
  }

  enum class Color {
    Black,
    White,
  }

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
  interface Piece {

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
