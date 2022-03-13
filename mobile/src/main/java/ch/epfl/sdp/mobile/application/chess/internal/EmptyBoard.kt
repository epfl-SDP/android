package ch.epfl.sdp.mobile.application.chess.internal

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position

/** An implementation of [Board] which contains no pieces at all. */
object EmptyBoard : Board {
  override fun get(position: Position): Piece? = null
}
