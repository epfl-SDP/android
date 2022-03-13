package ch.epfl.sdp.mobile.application.chess

/**
 * A class representing some valid coordinates on a board. Coordinates start at the top-left corner
 * of the board, and the x axis increases towards the right while the y axis increases towards the
 * bottom.
 *
 * @param x the coordinate on the first axis.
 * @param y the coordinate on the second axis.
 */
data class Position(val x: Int, val y: Int) {

  /** Returns true iff the [x] and the [y] coordinates are both the board bounds. */
  val inBounds: Boolean = x in AxisBounds && y in AxisBounds

  /** Adds a [Delta] to the [Position], and returns it only if the result is in bounds. */
  operator fun plus(delta: Delta): Position? {
    return Position(x + delta.x, y + delta.y).takeIf { inBounds }
  }

  companion object {

    /** The bounds which correspond to the valid positions on a board. */
    private val AxisBounds = 0..7

    /** Returns a [Sequence] with all the valid [Position] within a board. */
    fun all(): Sequence<Position> = sequence {
      for (i in AxisBounds) {
        for (j in AxisBounds) {
          yield(Position(i, j))
        }
      }
    }
  }
}

/**
 * A class representing the difference between two [Position].
 *
 * @param x the delta on the first axis.
 * @param y the delta on the second axis.
 */
data class Delta(val x: Int, val y: Int)
