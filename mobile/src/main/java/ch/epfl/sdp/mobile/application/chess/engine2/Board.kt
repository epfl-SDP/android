package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine2.core.Color as CoreColor
import ch.epfl.sdp.mobile.application.chess.engine2.core.MutableBoard as CoreMutableBoard
import ch.epfl.sdp.mobile.application.chess.engine2.core.Piece as CorePiece
import ch.epfl.sdp.mobile.application.chess.engine2.core.Position as CorePosition
import ch.epfl.sdp.mobile.application.chess.engine2.core.ranks.*

/**
 * Transforms this [Board] to a [CoreMutableBoard].
 *
 * @receiver the [Board] with some [Piece] of [Color].
 */
fun Board<Piece<Color>>.toMutableBoard(): CoreMutableBoard {
  val board = CoreMutableBoard()
  for ((pos, piece) in this) {
    val corePiece =
        CorePiece(
            id = piece.id.value,
            rank = piece.rank.toRank(),
            color = piece.color.toColor(),
        )
    board[pos.toPosition()] = corePiece
  }
  return board
}

/** Transforms this [CoreMutableBoard] to a [Board] with some [Piece] o [Color]. */
fun CoreMutableBoard.toBoard(): Board<Piece<Color>> = buildBoard {
  forEachPiece { (x, y), piece -> piece.toPiece()?.let { set(Position(x, y), it) } }
}

fun Position.toPosition() = CorePosition(x, y)

fun Color.toColor() =
    when (this) {
      Color.Black -> CoreColor.Black
      Color.White -> CoreColor.White
    }

fun Rank.toRank() =
    when (this) {
      Rank.King -> King
      Rank.Queen -> Queen
      Rank.Rook -> Rook
      Rank.Bishop -> Bishop
      Rank.Knight -> Knight
      Rank.Pawn -> Pawn
    }

/** Maps this [CorePiece] to the corresponding [Piece] of [Color]. */
private fun CorePiece.toPiece(): Piece<Color>? {
  val color =
      when (color) {
        CoreColor.Black -> Color.Black
        CoreColor.White -> Color.White
        else -> return null
      }
  val rank =
      when (rank) {
        is Bishop -> Rank.Bishop
        is King -> Rank.King
        is Queen -> Rank.Queen
        is Rook -> Rank.Rook
        is Pawn -> Rank.Pawn
        is Knight -> Rank.Knight
        else -> return null
      }
  return Piece(color, rank, PieceIdentifier(id))
}
