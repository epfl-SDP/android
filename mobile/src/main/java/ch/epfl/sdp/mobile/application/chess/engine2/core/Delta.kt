package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.E
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.N
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.NE
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.NW
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.S
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.SE
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.SW
import ch.epfl.sdp.mobile.application.chess.engine2.core.Delta.CardinalPoints.W
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.packShorts
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort1
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort2

/**
 * A class representing the difference between two [Position].
 *
 * @param backing the backing field for the inline class.
 */
@JvmInline
value class Delta private constructor(private val backing: Int) {

  /**
   * Creates a new [Delta].
   *
   * @param x the delta on the first axis.
   * @param y the delta on the second axis.
   */
  constructor(x: Int, y: Int) : this(packShorts(x.toShort(), y.toShort()))

  /** The delta on the first axis. */
  val x: Int
    get() = unpackShort1(backing).toInt()

  /** The delta on the second axis. */
  val y: Int
    get() = unpackShort2(backing).toInt()

  /** Multiplies this [Delta] with a factor along the [x] and [y] axis. */
  operator fun times(scalar: Int): Delta = Delta(x * scalar, y * scalar)

  /** Adds an [other] [Delta] to this [Delta]. */
  operator fun plus(other: Delta): Delta = Delta(x + other.x, y + other.y)

  /** An object with all the cardinal directions. */
  object CardinalPoints {

    /** Moves towards the top. */
    val N = Delta(0, -1)

    /** Moves towards the bottom. */
    val S = Delta(0, 1)

    /** Moves towards the right. */
    val E = Delta(1, 0)

    /** Moves towards the top right. */
    val NE = N + E

    /** Moves towards the bottom right. */
    val SE = S + E

    /** Moves towards the left. */
    val W = Delta(-1, 0)

    /** Moves towards the top left. */
    val NW = N + W

    /** Moves towards the bottom left. */
    val SW = S + W
  }

  /** An object with some standard directions. */
  object Directions {

    /** Moves on lines. */
    val Lines = listOf(E, S, W, N)

    /** Moves on diagonals. */
    val Diagonals = listOf(NE, SE, NW, SW)
  }
}
