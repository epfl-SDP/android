package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.implementation.MutableBoard
import ch.epfl.sdp.mobile.application.chess.engine.utils.packShorts
import ch.epfl.sdp.mobile.application.chess.engine.utils.unpackShort1
import ch.epfl.sdp.mobile.application.chess.engine.utils.unpackShort2

/**
 * A class representing some valid coordinates on a board. Coordinates start at the top-left corner
 * of the board, and the x axis increases towards the right while the y axis increases towards the
 * bottom.
 *
 * @property backing the backing field for the inline class.
 */
@JvmInline
value class Position private constructor(private val backing: Int) {

  /**
   * Creates a new [Position].
   *
   * @param x the coordinate on the first axis.
   * @param y the coordinate on the second axis.
   */
  constructor(x: Int, y: Int) : this(packShorts(x.toShort(), y.toShort()))

  /** Returns true iff the [x] and [y] coordinates are in bounds. */
  val inBounds: Boolean
    get() = x in MutableBoard.AxisBounds && y in MutableBoard.AxisBounds

  /** Returns the [x] coordinate of the [Position]. */
  val x: Int
    get() = unpackShort1(backing).toInt()

  /** Returns the [y] coordinate of the [Position]. */
  val y: Int
    get() = unpackShort2(backing).toInt()

  /** Returns the [x] coordinate. */
  operator fun component1(): Int = x

  /** Returns the [y] coordinate. */
  operator fun component2(): Int = y

  /**
   * Adds a [Delta] to this [Position].
   *
   * @param delta the [Delta] which is added.
   * @return the [Position] with the [Delta].
   */
  operator fun plus(delta: Delta): Position = Position(delta.x + x, delta.y + y)

  /**
   * Removes a [Delta] to this [Position].
   *
   * @param delta the [Delta] which is removed.
   * @return the [Position] without the [Delta].
   */
  operator fun minus(delta: Delta): Position = Position(x - delta.x, y - delta.y)

  /**
   * Returns the difference between two [Position].
   *
   * @param other the other [Position].
   * @return the [Delta] to apply to this [Position] to obtain the [other] position.
   */
  operator fun minus(other: Position): Delta = Delta(x - other.x, y - other.y)

  companion object {

    /** Returns a [Sequence] with all the valid [Position] within a board. */
    fun all(): Sequence<Position> = sequence {
      MutableBoard.AxisBounds.forEach { i ->
        MutableBoard.AxisBounds.forEach { j -> yield(Position(i, j)) }
      }
    }
  }
}
