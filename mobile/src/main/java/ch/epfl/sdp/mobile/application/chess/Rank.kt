package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.chess.moves.*

/**
 * An enumeration representing the abilities of each [Piece] in classic chess.
 *
 * @param moves the moves and threats that each piece may perform, depending on the current board
 * configuration.
 *
 * TODO : Support all the moves.
 */
enum class Rank(
    val moves: Board<Piece<Role>>.(Position) -> Moves,
) {
  King(moves = { diagonals(it, 1) + lines(it, 1) }),
  Queen(moves = { diagonals(it) + lines(it) }),
  Rook(moves = { lines(it) }),
  Bishop(moves = { diagonals(it) }),
  Knight(moves = { none() }),
  Pawn(moves = { delta(it, x = 0, y = -1) + delta(it, x = 0, y = -2) }),
}
