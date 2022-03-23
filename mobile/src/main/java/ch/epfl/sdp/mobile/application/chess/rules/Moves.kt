package ch.epfl.sdp.mobile.application.chess.rules

import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.Rank.*
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.combine
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.move
import ch.epfl.sdp.mobile.application.chess.rules.Effect.Factory.remove
import ch.epfl.sdp.mobile.application.chess.rules.Role.Adversary
import ch.epfl.sdp.mobile.application.chess.rules.Role.Allied

/**
 * An alias which describes a sequence of boards. The most recent board will be made available as
 * the [last] element of the sequence.
 *
 * @param P the type of the pieces of the board.
 */
typealias BoardSequence<P> = Sequence<Board<P>>

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
fun BoardSequence<Piece<Role>>.delta(
    from: Position,
    x: Int,
    y: Int,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val board = first()
  val delta = Delta(x, y)
  val target = from + delta ?: return@sequence // Stop if out of bounds.
  val piece = board[target]
  if (piece == null || (includeAdversary && piece.color == Adversary)) {
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
fun BoardSequence<Piece<Role>>.doubleUp(
    from: Position,
    row: Int = 6,
    includeAdversary: Boolean = false,
): Moves = sequence {
  val board = first()
  if (from.y != row) return@sequence // Not on the right row.
  if (board[Position(from.x, from.y - 1)] != null) return@sequence // A piece is in the path.
  yieldAll(delta(from, x = 0, y = -2, includeAdversary = includeAdversary))
}

/**
 * A [Moves] implementation which will let pawns take pieces in diagonal.
 *
 * @param from the original [Position] of the pawn.
 */
fun BoardSequence<Piece<Role>>.sideTakes(
    from: Position,
): Moves = sequence {
  val board = first()
  if (board[Position(from.x - 1, from.y - 1)] != null) yieldAll(delta(from, -1, -1))
  if (board[Position(from.x + 1, from.y - 1)] != null) yieldAll(delta(from, 1, -1))
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
private fun BoardSequence<Piece<Role>>.repeatDirection(
    from: Position,
    direction: Delta,
    maxRepeats: Int = Board.Size,
    includeAdversary: Boolean = true,
): Moves = sequence {
  val board = first()
  repeat(maxRepeats) { i ->
    val delta = direction * (i + 1)
    val target = from + delta ?: return@sequence // Stop if out of bounds.
    val piece = board[target]
    val action = Action(from, delta)
    if (piece != null) {
      if (includeAdversary && piece.color == Adversary) {
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
fun BoardSequence<Piece<Role>>.lines(
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
fun BoardSequence<Piece<Role>>.diagonals(
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
fun BoardSequence<Piece<Role>>.leftCastling() =
    castling(
        kingStart = Position(x = 4, y = 7),
        kingEnd = Position(x = 2, y = 7),
        rookStart = Position(x = 0, y = 7),
        rookEnd = Position(x = 3, y = 7),
        empty = setOf(Position(x = 1, y = 7), Position(x = 2, y = 7), Position(x = 3, y = 7)),
    )

/** Moves representing a castling to the right. */
fun BoardSequence<Piece<Role>>.rightCastling() =
    castling(
        kingStart = Position(x = 4, y = 7),
        kingEnd = Position(x = 6, y = 7),
        rookStart = Position(x = 7, y = 7),
        rookEnd = Position(x = 5, y = 7),
        empty = setOf(Position(x = 5, y = 7), Position(x = 6, y = 7)),
    )

private fun BoardSequence<Piece<Role>>.castling(
    kingStart: Position,
    kingEnd: Position,
    rookStart: Position,
    rookEnd: Position,
    empty: Set<Position>,
): Moves = sequence {
  val board = first()
  val king = board[kingStart]?.takeIf { (role, rank) -> role == Allied && rank == King }
  val rook = board[rookStart]?.takeIf { (role, rank) -> role == Allied && rank == Rook }

  // Ensure that the kind and rook are actually present.
  king ?: return@sequence
  rook ?: return@sequence

  // If any of the cells is not empty, we can't castle.
  // TODO : Check if these cells are in check.
  if (empty.any { board[it] != null }) return@sequence

  // Check that the king and rook have never moved.
  if (any { it[kingStart] != king || it[rookStart] != rook }) return@sequence

  // We can perform the castling.
  yield(
      Action(kingStart, kingEnd - kingStart) to
          combine(
              move(kingStart, kingEnd - kingStart),
              move(rookStart, rookEnd - rookStart),
          ),
  )
}

/**
 * Moves representing an en-passant take, which may be performed by the current piece on existing
 * pawn.
 *
 * @param position the position at which the current piece is.
 * @param delta the relative position to which the adversary pawn is.
 */
fun BoardSequence<Piece<Role>>.enPassant(
    position: Position,
    delta: Delta,
): Moves = sequence {
  val board = first()
  val neighbour = position + delta ?: return@sequence
  val adversary = board[neighbour]?.takeIf { (role, rank) -> role == Adversary && rank == Pawn }

  // Do we have two pawns next to each other ?
  adversary ?: return@sequence

  // Are we on the right row to perform an en-passant ?
  if (position.y != 3) return@sequence

  // Are the neighbour positions valid ?
  val adversaryStep = neighbour + Delta(x = 0, y = -1) ?: return@sequence
  val adversaryStart = neighbour + Delta(x = 0, y = -2) ?: return@sequence

  // Check that the adversary stayed on their starting position for the whole game, except for the
  // previous move.
  if (drop(1).any { it[adversaryStart] != adversary }) return@sequence

  yield(
      Action(position, adversaryStep - position) to
          combine(
              remove(neighbour),
              move(position, adversaryStep - position),
          ),
  )
}
