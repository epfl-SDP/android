package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.rules.*

/**
 * An enumeration representing the abilities of each [Piece] in classic chess.
 *
 * @param moves the moves and threats that each piece may perform, depending on the current board
 * configuration.
 *
 * TODO : Support check-awareness in moves.
 */
enum class Rank(
    val moves: BoardSequence<Piece<Role>>.(Position) -> Moves<Piece<Role>>,
) {
  King(moves = { diagonals(it, 1) + lines(it, 1) + leftCastling() + rightCastling() }),
  Queen(moves = { diagonals(it) + lines(it) }),
  Rook(moves = { lines(it) }),
  Bishop(moves = { diagonals(it) }),
  Knight(
      moves = {
        delta(it, 1, -2) +
            delta(it, 2, -1) +
            delta(it, 2, 1) +
            delta(it, 1, 2) +
            delta(it, -1, 2) +
            delta(it, -2, 1) +
            delta(it, -2, -1) +
            delta(it, -1, -2)
      },
  ),
  Pawn(
      moves = {
        moveUpOrPromote(it) +
            doubleUp(it) +
            sideTakes(it) +
            enPassant(it, Delta(x = 1, y = 0)) +
            enPassant(it, Delta(x = -1, y = 0))
      },
  ),
}
