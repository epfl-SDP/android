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
    val moves: GameWithRoles.() -> Moves,
) {
  King(moves = { diagonals(1) + lines(1) }),
  Queen(moves = { diagonals() + lines() }),
  Rook(moves = { lines() }),
  Bishop(moves = { diagonals() }),
  Knight(moves = { none() }),
  Pawn(moves = { delta(0, 1) + delta(0, 2) }),
}
