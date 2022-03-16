package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/**
 * An interface which represents the state of a [ChessBoard] composable. It display the current
 * state of the game, and offers some ways for the [ChessBoard] to interact with it and indicate the
 * user gestures.
 *
 * Each [ChessBoardState.Piece] has unique [Identifier], which is used to display some smooth
 * animations if multiple pieces with the same ranks and colors exist.
 *
 * @param Identifier the type of the identifiers of each piece.
 */
@Stable
interface ChessBoardState<Identifier> {

  /** The different ranks which may be displayed by a [ChessBoard]. */
  enum class Rank {
    King,
    Queen,
    Rook,
    Bishop,
    Knight,
    Pawn,
  }

  /** The different colors which may be displayed by a [ChessBoard]. */
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

  /**
   * A class representing a [Piece] that may be displayed on a [ChessBoard].
   *
   * @param id the unique identifier for this [Piece]. Two different pieces with the same rank and
   * color must have different identifiers.
   * @param rank the rank of the piece.
   * @param color the color of the piece.
   */
  data class Piece<out Identifier>(
      val id: Identifier,
      val rank: Rank,
      val color: Color,
  )

  /** A [Map] of the [Piece], associated to their [Position] on the board. */
  val pieces: Map<Position, Piece<Identifier>>

  /**
   * A callback which should be called when the given [Piece] was moved from a start position to an
   * end position.
   *
   * @param piece the [Piece] that was moved.
   * @param endPosition the place where the [Piece] was dropped.
   */
  fun onDropPiece(piece: Piece<Identifier>, endPosition: Position)
}
