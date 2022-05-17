package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.NormalizedBoardDecorator

/** Returns true iff the [Board] has any move available for any piece of the given [Color]. */
fun <P : Player<P>> BoardSequence<Piece<P>>.hasAnyMoveAvailable(color: P): Boolean {
  return allMoves(color).any { (_, effect) ->
    !(sequenceOf(effect.perform(first())) + this).inCheck(color)
  }
}

/** Returns true iff the [Board] has at least one piece of the given [Rank] and [Color]. */
fun <Color> Board<Piece<Color>>.hasAnyOfRankAndColor(rank: Rank, player: Color): Boolean {
  return asSequence().any { (_, piece) -> piece.rank == rank && piece.color == player }
}

/**
 * Returns true if and only if the player with the given color is in check.
 *
 * @param player the [Color] of the player for whom the check is checked.
 */
fun <P : Player<P>> BoardSequence<Piece<P>>.inCheck(player: P): Boolean {
  return allMoves(player.other()).any { (_, effect) ->
    !effect.perform(first()).hasAnyOfRankAndColor(Rank.King, player)
  }
}

/**
 * Computes all the [Moves] which can be performed by a player of the given [color].
 *
 * @param color the [Color] of the player for whom all the moves are computed.
 */
fun <P : Player<P>> BoardSequence<Piece<P>>.allMoves(color: P): Moves<Piece<P>> {
  return first().asSequence().flatMap { (pos, _) -> allMovesAtPosition(color, pos) }
}

/**
 * Returns all of the moves that may be originating from the given [position], considering that the
 * current player is of a specific [color].
 *
 * @param color the [Color] of the player whose turn is next.
 * @param position the [Position] for which the possible [Moves] are computed.
 */
fun <P : Player<P>> BoardSequence<Piece<P>>.allMovesAtPosition(
    color: P,
    position: Position,
): Moves<Piece<P>> {
  val normalizedBoards = map { NormalizedBoardDecorator(color, it) }
  val normalizedFrom = color.normalize(position)
  val piece = normalizedBoards.first()[normalizedFrom] ?: return emptySequence()
  if (piece.color == Role.Adversary) return emptySequence()
  val moves = piece.rank.moves(normalizedBoards, normalizedFrom)
  return moves.map { (action, effect) -> color.normalize(action) to color.denormalize(effect) }
}
