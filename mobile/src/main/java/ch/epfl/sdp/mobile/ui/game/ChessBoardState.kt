package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.BlackKing
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * An interface which represents the state of a [ChessBoard] composable. It display the current
 * state of the game, and offers some ways for the [ChessBoard] to interact with it and indicate the
 * user gestures.
 *
 * Each [ChessBoardState.Piece] has unique [ChessBoardState.Piece], which is used to display some
 * smooth animations if multiple pieces with the same ranks and colors exist.
 *
 * @param Piece the type of the [ChessBoardState.Piece].
 */
@Stable
interface ChessBoardState<Piece : ChessBoardState.Piece> {

  /**
   * The different ranks which may be displayed by a [ChessBoard].
   *
   * @param contentDescription the content description for this rank.
   * @param whiteIcon the icon for the white variant of this rank.
   * @param blackIcon the icon for the black variant of this rank.
   */
  enum class Rank(
      val contentDescription: LocalizedStrings.() -> String,
      val whiteIcon: @Composable () -> Painter,
      val blackIcon: @Composable () -> Painter,
  ) {
    King(
        contentDescription = { boardPieceKing },
        whiteIcon = { ChessIcons.WhiteKing },
        blackIcon = { ChessIcons.BlackKing },
    ),
    Queen(
        contentDescription = { boardPieceQueen },
        whiteIcon = { ChessIcons.WhiteQueen },
        blackIcon = { ChessIcons.BlackQueen },
    ),
    Rook(
        contentDescription = { boardPieceRook },
        whiteIcon = { ChessIcons.WhiteRook },
        blackIcon = { ChessIcons.BlackRook },
    ),
    Bishop(
        contentDescription = { boardPieceBishop },
        whiteIcon = { ChessIcons.WhiteBishop },
        blackIcon = { ChessIcons.BlackBishop },
    ),
    Knight(
        contentDescription = { boardPieceKnight },
        whiteIcon = { ChessIcons.WhiteKnight },
        blackIcon = { ChessIcons.BlackKnight },
    ),
    Pawn(
        contentDescription = { boardPiecePawn },
        whiteIcon = { ChessIcons.WhitePawn },
        blackIcon = { ChessIcons.BlackPawn },
    ),
  }

  /**
   * The different colors which may be displayed by a [ChessBoard].
   *
   * @param contentDescription the content description for this color.
   */
  enum class Color(
      val contentDescription: LocalizedStrings.() -> String,
  ) {
    Black(contentDescription = { boardColorBlack }),
    White(contentDescription = { boardColorWhite }),
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

  /** Returns the position currently selected by the user, if there's any. */
  val selectedPosition: Position?

  /** Returns the position of the [Rank.King] currently in check, if there's any. */
  val checkPosition: Position?

  /**
   * A [Set] of the positions which are available to the player for actions, depending on the pieces
   * which they are currently holding.
   */
  val availableMoves: Set<Position>

  /**
   * A callback which should be called when the given [Piece] was moved from a start position to an
   * end position.
   *
   * @param piece the [Piece] that was moved.
   * @param endPosition the place where the [Piece] was dropped.
   */
  fun onDropPiece(piece: Piece, endPosition: Position)

  /**
   * A callback which will be called when the [ChessBoard] is clicked at the given position. A
   * clicked [ChessBoard] might indicate that the user wants to play a piece.
   *
   * @param position the place that was clicked on the board.
   */
  fun onPositionClick(position: Position)
}
