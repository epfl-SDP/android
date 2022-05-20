package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.packShorts
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort1
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort2

/** The range of possible values for an axis. */
private val AxisRange = 0 until MutableBoard.Size

/**
 * A class representing some valid coordinates on a board. Coordinates start at the top-left corner
 * of the board, and the x axis increases towards the right while the y axis increases towards the
 * bottom.
 *
 * @param backing the backing field for the inline class.
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
    get() = x in AxisRange && y in AxisRange

  /** Returns the [x] coordinate of the [Position]. */
  val x: Int
    get() = unpackShort1(backing).toInt()

  /** Returns the [y] coordinate of the [Position]. */
  val y: Int
    get() = unpackShort2(backing).toInt()

  operator fun component1(): Int = x
  operator fun component2(): Int = y

  /**
   * Adds a [Delta] to this [Position].
   *
   * @param delta the [Delta] which is added.
   * @return the [Position] with the [Delta].
   */
  operator fun plus(delta: Delta): Position = Position(delta.x + x, delta.y + y)
}
