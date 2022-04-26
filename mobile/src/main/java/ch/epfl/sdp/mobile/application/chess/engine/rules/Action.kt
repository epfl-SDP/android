package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank

/**
 * An [Action] represents a semantic interaction of one player with the game. Actions have a start
 * position (corresponding to a piece that the player wants to play), and a delta, corresponding to
 * where they position the piece on the board (relative to the start position).
 *
 * Multiple [Action] may be defined for the same start and end position. In this case, some
 * additional data in the [Action] will be present to disambiguate the differences between the
 * actions.
 */
sealed interface Action {

  /** The position from which the piece is dragged. */
  val from: Position

  /** The amount by which the piece is moved. */
  val delta: Delta

  // Convenience operators to keep a destructuring-friendly syntax.
  operator fun component1(): Position = from
  operator fun component2(): Delta = delta

  /**
   * An [Action] which represents the act of moving a piece on the board.
   *
   * @param from the start position.
   * @param delta the applied delta.
   */
  data class Move(override val from: Position, override val delta: Delta) : Action {

    /**
     * A convenience constructor for the [Move] action.
     *
     * @param from the start position.
     * @param to the end position.
     */
    constructor(from: Position, to: Position) : this(from, to - from)
  }

  /**
   * An [Action] which represents the act of promoting a pawn to a piece with a certain rank.
   *
   * @param from the start position.
   * @param delta the applied delta.
   * @param rank the chosen [Rank].
   */
  data class Promote(
      override val from: Position,
      override val delta: Delta,
      val rank: Rank,
  ) : Action {

    /**
     * A convenience constructor for the [Promote] action.
     *
     * @param from the start position.
     * @param to the end position.
     * @param rank the chosen [Rank]
     */
    constructor(from: Position, to: Position, rank: Rank) : this(from, to - from, rank)
  }
}
