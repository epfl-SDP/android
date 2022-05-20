package ch.epfl.sdp.mobile.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine2.core.MutableBoard as CoreMutableBoard
import ch.epfl.sdp.mobile.application.chess.engine2.core.Piece as CorePiece
import ch.epfl.sdp.mobile.application.chess.engine2.core.ranks.*

/**
 * Transforms this [Board] to a [CoreMutableBoard].
 *
 * @receiver the [Board] with some [Piece] of [Color].
 */
fun Board<Piece<Color>>.toMutableBoard(): CoreMutableBoard {
  val board = CoreMutableBoard()
  for ((pos, piece) in this) {
    board[pos] =
        CorePiece(
            id = piece.id.value,
            rank = piece.rank,
            color = piece.color,
        )
  }
  return board
}

/** Transforms this [CoreMutableBoard] to a [Board] with some [Piece] o [Color]. */
fun CoreMutableBoard.toBoard(): Board<Piece<Color>> = buildBoard {
  forEachPiece { (x, y), piece -> piece.toPiece()?.let { set(Position(x, y), it) } }
}

/** Maps this [CorePiece] to the corresponding [Piece] of [Color]. */
private fun CorePiece.toPiece(): Piece<Color>? {
  val color = color ?: return null
  val rank = rank ?: return null
  return Piece(color, rank, PieceIdentifier(id))
}
