package ch.epfl.sdp.mobile.application.chess.moves

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.rules.Effect
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.move

/**
 * An alias which which describes a lazily-built collection of moves in the game. Each move has an
 * associated [Action], which describes what gesture that the player will have performed on the
 * screen to reach this state.
 */
typealias Moves = Sequence<Pair<Action, Effect<Piece<Role>>>>

/**
 * Moves along the lines, until either [maxDistance] or a piece is reached.
 *
 * @param maxDistance the maximum moving distance for the piece.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun GameWithRoles.lines(
    maxDistance: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val directions = listOf(Delta(0, 1), Delta(0, -1), Delta(1, 0), Delta(-1, 0))
  for (direction in directions) {
    yieldAll(
        repeatDirection(
            direction = direction,
            maxRepeats = maxDistance,
            includeAdversary = includeAdversary,
        ),
    )
  }
}

/** Represents some [Moves] indicating that this piece may not perform any action. */
fun GameWithRoles.none(): Moves = emptySequence()

/**
 * Moves along the diagonals, until either [maxDistance] or a piece is reached .
 *
 * @param maxDistance the maximum moving distance for the piece.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun GameWithRoles.diagonals(
    maxDistance: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val directions = listOf(Delta(-1, -1), Delta(-1, 1), Delta(1, -1), Delta(1, 1))
  for (direction in directions) {
    yieldAll(
        repeatDirection(
            direction = direction,
            maxRepeats = maxDistance,
            includeAdversary = includeAdversary,
        ),
    )
  }
}

/**
 * A simple [Moves] implementation which represents a displacement of the [Piece] with a certain
 * step.
 *
 * @param x the delta on the x axis.
 * @param y the delta on the y axis.
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
fun GameWithRoles.delta(
    x: Int,
    y: Int,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val delta = Delta(x, y)
  val target = position + delta ?: return@sequence // Stop if out of bounds.
  val piece = get(target)
  if (piece == null || (includeAdversary && piece.color == Role.Adversary)) {
    yield(Action(position, delta) to move(position, delta))
  }
}

/**
 * Repeats the given [Delta] from 1 to [maxRepeats] (excluded) times. If an adversary is encountered
 * or the target gets out of bounds, the repeat process will be stopped.
 *
 * @param direction the [Delta] to be applied to each step.
 * @param maxRepeats the maximum number of steps in the [direction].
 * @param includeAdversary true if the [Moves] should include stepping on and eating adversaries.
 */
private fun GameWithRoles.repeatDirection(
    direction: Delta,
    maxRepeats: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  for (i in 1..maxRepeats) {
    val delta = direction * i
    val target = position + delta ?: return@sequence // Stop if out of bounds.
    val piece = get(target)
    val action = Action(position, delta)
    if (piece != null) {
      if (includeAdversary && piece.color == Role.Adversary) {
        yield(action to move(position, delta))
      }
      return@sequence
    }
    yield(action to move(position, delta))
  }
}
