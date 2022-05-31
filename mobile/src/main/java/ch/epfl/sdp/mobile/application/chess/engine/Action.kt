package ch.epfl.sdp.mobile.application.chess.engine

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

  /** Returns the [Position] of this [Action]. */
  operator fun component1(): Position = from

  /** Returns the [Delta] of this [Action]. */
  operator fun component2(): Delta = delta

  companion object {

    /**
     * A convenience constructor for the [Move] action.
     *
     * @param from the start position.
     * @param to the end position.
     */
    // This must be a function because the inline classes signatures would otherwise clash.
    @JvmStatic
    fun Move(from: Position, to: Position): Move {
      return Move(from, to - from)
    }

    /**
     * A convenience constructor for the [Promote] action.
     *
     * @param from the start position.
     * @param to the end position.
     * @param rank the chosen [Rank]
     */
    // This must be a function because the inline classes signatures would otherwise clash.
    @JvmStatic
    fun Promote(from: Position, to: Position, rank: Rank): Promote {
      return Promote(from, to - from, rank)
    }
  }

  /**
   * An [Action] which represents the act of moving a piece on the board.
   *
   * @param from the start position.
   * @param delta the applied delta.
   */
  data class Move(override val from: Position, override val delta: Delta) : Action

  /**
   * An [Action] which represents the act of promoting a pawn to a piece with a certain rank.
   *
   * @property from the start position.
   * @property delta the applied delta.
   * @property rank the chosen [Rank].
   */
  data class Promote(
      override val from: Position,
      override val delta: Delta,
      val rank: Rank,
  ) : Action
}
