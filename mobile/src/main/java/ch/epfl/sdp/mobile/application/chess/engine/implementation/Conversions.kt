package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.*

/**
 * Transforms this [Board] to a [CoreMutableBoard].
 *
 * @receiver the [Board] with some [Piece] of [Color].
 */
fun Board<Piece<Color>>.toMutableBoard(): MutableBoard {
  val board = MutableBoard()
  for ((pos, piece) in this) {
    board[pos] =
        MutableBoardPiece(
            id = piece.id,
            rank = piece.rank,
            color = piece.color,
        )
  }
  return board
}

/** Transforms this [MutableBoard] to a [Board] with some [Piece] o [Color]. */
fun MutableBoard.toBoard(): Board<Piece<Color>> = buildBoard {
  forEachPiece { (x, y), piece -> piece.toPiece()?.let { set(Position(x, y), it) } }
}

/** Maps this [MutableBoardPiece] to the corresponding [Piece] of [Color]. */
fun MutableBoardPiece.toPiece(): Piece<Color>? {
  val color = color ?: return null
  val rank = rank ?: return null
  return Piece(color, rank, PieceIdentifier(id))
}

/** Maps this [Piece] to the corresponding [MutableBoardPiece]. */
fun Piece<Color>?.toMutableBoardPiece(): MutableBoardPiece {
  this ?: return MutableBoardPiece.None
  return MutableBoardPiece(id, rank, color)
}
