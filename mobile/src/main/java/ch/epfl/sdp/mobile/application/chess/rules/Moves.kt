package ch.epfl.sdp.mobile.application.chess.rules

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.Rank.King
import ch.epfl.sdp.mobile.application.chess.Rank.Rook
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.all
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.move
import ch.epfl.sdp.mobile.application.chess.rules.Role.Allied

/**
 * An alias which which describes a lazily-built collection of moves in the game. Each move has an
 * associated [Action], which describes what gesture that the player will have performed on the
 * screen to reach this state.
 */
typealias Moves = Sequence<Pair<Action, Effect<Piece<Role>>>>

/**
 * A simple [Moves] implementation which represents a displacement of the [Piece] with a certain
 * step.
 *
 * @param from the original [Position].
 * @param x the delta on the x axis.
 * @param y the delta on the y axis.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun Board<Piece<Role>>.delta(
    from: Position,
    x: Int,
    y: Int,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val delta = Delta(x, y)
  val target = from + delta ?: return@sequence // Stop if out of bounds.
  val piece = get(target)
  if (piece == null || (includeAdversary && piece.color == Role.Adversary)) {
    yield(Action(from, delta) to move(from, delta))
  }
}

/**
 * A [Moves] implementation which will usually be applied for the first turn of the pawns, when they
 * are allowed to move up by two moves.
 *
 * @param from the original [Position].
 * @param row the row on which the double-up move is allowed.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun Board<Piece<Role>>.doubleUp(
    from: Position,
    row: Int = 6,
    includeAdversary: Boolean = false,
): Moves = sequence {
  if (from.y != row) return@sequence // Not on the right row.
  if (get(Position(from.x, from.y - 1)) != null) return@sequence // A piece is in the path.
  yieldAll(delta(from, x = 0, y = -2, includeAdversary = includeAdversary))
}

/**
 * A [Moves] implementation which will let pawns take pieces in diagonal.
 *
 * @param from the original [Position] of the pawn.
 */
fun Board<Piece<Role>>.sideTakes(
    from: Position,
): Moves = sequence {
  if (get(Position(from.x - 1, from.y - 1)) != null) yieldAll(delta(from, -1, -1))
  if (get(Position(from.x + 1, from.y - 1)) != null) yieldAll(delta(from, 1, -1))
}

/**
 * Repeats the given [Delta] from 1 to [maxRepeats] (excluded) times. If an adversary is encountered
 * or the target gets out of bounds, the repeat process will be stopped.
 *
 * @param from the original [Position].
 * @param direction the [Delta] to be applied to each step.
 * @param maxRepeats the maximum number of steps in the [direction].
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
private fun Board<Piece<Role>>.repeatDirection(
    from: Position,
    direction: Delta,
    maxRepeats: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  repeat(maxRepeats) { i ->
    val delta = direction * (i + 1)
    val target = from + delta ?: return@sequence // Stop if out of bounds.
    val piece = get(target)
    val action = Action(from, delta)
    if (piece != null) {
      if (includeAdversary && piece.color == Role.Adversary) {
        yield(action to move(from, delta))
      }
      return@sequence
    }
    yield(action to move(from, delta))
  }
}

/**
 * Moves along the lines, until either [maxDistance] or a piece is reached.
 *
 * @param from the original [Position].
 * @param maxDistance the maximum moving distance for the piece.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun Board<Piece<Role>>.lines(
    from: Position,
    maxDistance: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val directions = listOf(Delta(0, 1), Delta(0, -1), Delta(1, 0), Delta(-1, 0))
  for (direction in directions) {
    yieldAll(
        repeatDirection(
            from = from,
            direction = direction,
            maxRepeats = maxDistance,
            includeAdversary = includeAdversary,
        ),
    )
  }
}

/**
 * Moves along the diagonals, until either [maxDistance] or a piece is reached .
 *
 * @param from the original [Position].
 * @param maxDistance the maximum moving distance for the piece.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun Board<Piece<Role>>.diagonals(
    from: Position,
    maxDistance: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val directions = listOf(Delta(-1, -1), Delta(-1, 1), Delta(1, -1), Delta(1, 1))
  for (direction in directions) {
    yieldAll(
        repeatDirection(
            from = from,
            direction = direction,
            maxRepeats = maxDistance,
            includeAdversary = includeAdversary,
        ),
    )
  }
}

/** Moves representing a castling to the left. */
fun BoardWithHistory<Piece<Role>>.leftCastling() =
    castling(
        kingStart = Position(x = 4, y = 7),
        kingEnd = Position(x = 2, y = 7),
        rookStart = Position(x = 0, y = 7),
        rookEnd = Position(x = 3, y = 7),
        empty = setOf(Position(x = 1, y = 7), Position(x = 2, y = 7), Position(x = 3, y = 7)),
    )

/** Moves representing a castling to the right. */
fun BoardWithHistory<Piece<Role>>.rightCastling() =
    castling(
        kingStart = Position(x = 4, y = 7),
        kingEnd = Position(x = 6, y = 7),
        rookStart = Position(x = 7, y = 7),
        rookEnd = Position(x = 5, y = 7),
        empty = setOf(Position(x = 5, y = 7), Position(x = 6, y = 7)),
    )

private fun BoardWithHistory<Piece<Role>>.castling(
    kingStart: Position,
    kingEnd: Position,
    rookStart: Position,
    rookEnd: Position,
    empty: Set<Position>,
): Moves = sequence {
  val king = get(kingStart)?.takeIf { (role, rank) -> role == Allied && rank == King }
  val rook = get(rookStart)?.takeIf { (role, rank) -> role == Allied && rank == Rook }

  // Ensure that the kind and rook are actually present.
  king ?: return@sequence
  rook ?: return@sequence

  // If any of the cells is not empty, we can't castle.
  // TODO : Check if these cells are in check.
  if (empty.any { get(it) != null }) return@sequence

  // Check that the king and rook have never moved.
  if (asSequence().any { it[kingStart] != king || it[rookStart] != rook }) return@sequence

  // We can perform the castling.
  yield(
      Action(kingStart, kingEnd - kingStart) to
          all(
              move(kingStart, kingEnd - kingStart),
              move(rookStart, rookEnd - rookStart),
          ),
  )
}
