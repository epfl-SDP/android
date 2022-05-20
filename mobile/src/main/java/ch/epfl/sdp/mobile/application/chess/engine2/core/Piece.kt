package ch.epfl.sdp.mobile.application.chess.engine2.core

@JvmInline
value class Piece private constructor(private val backing: Long) {
  val color: Color
    get() = TODO()
  val isUndefined: Boolean
    get() = true
}
