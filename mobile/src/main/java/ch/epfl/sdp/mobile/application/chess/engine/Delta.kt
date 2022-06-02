package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.E
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.N
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.NE
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.NW
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.S
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.SE
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.SW
import ch.epfl.sdp.mobile.application.chess.engine.Delta.CardinalPoints.W
import ch.epfl.sdp.mobile.application.chess.engine.utils.packShorts
import ch.epfl.sdp.mobile.application.chess.engine.utils.unpackShort1
import ch.epfl.sdp.mobile.application.chess.engine.utils.unpackShort2
import kotlin.math.sign

/**
 * A class representing the difference between two [Position].
 *
 * @property backing the backing field for the inline class.
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

  /** Returns a [Delta] which only keeps track of the signs along the x and y axis. */
  val sign: Delta
    get() = Delta(x.sign, y.sign)

  /** Reverses this [Delta]. */
  operator fun unaryMinus(): Delta = Delta(-x, -y)

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
