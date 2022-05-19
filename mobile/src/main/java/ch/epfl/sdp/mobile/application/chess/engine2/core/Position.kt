package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.packShorts
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort1
import ch.epfl.sdp.mobile.application.chess.engine2.core.utils.unpackShort2

private val AxisRange = 0 until MutableBoard.Size

@JvmInline
value class Position private constructor(private val backing: Int) {

  constructor(x: Int, y: Int) : this(packShorts(x.toShort(), y.toShort()))

  val inBounds: Boolean
    get() = x in AxisRange && y in AxisRange

  val x: Int
    get() = unpackShort1(backing).toInt()

  val y: Int
    get() = unpackShort2(backing).toInt()

  operator fun plus(delta: Delta): Position = Position(delta.x + x, delta.y + y)
}
