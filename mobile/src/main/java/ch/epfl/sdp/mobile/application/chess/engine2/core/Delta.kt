package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.packShorts
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort1
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort2

@JvmInline
value class Delta private constructor(private val backing: Int) {

  constructor(x: Int, y: Int) : this(packShorts(x.toShort(), y.toShort()))

  val x: Int
    get() = unpackShort1(backing).toInt()

  val y: Int
    get() = unpackShort2(backing).toInt()

  operator fun times(scalar: Int): Delta = Delta(x * scalar, y * scalar)

  operator fun plus(other: Delta): Delta = Delta(x + other.x, y + other.y)
}
