package ch.epfl.sdp.mobile.application.chess.rules

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.move

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
  for (i in 1..maxRepeats) {
    val delta = direction * i
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
