package ch.epfl.sdp.mobile.application.chess

/** A [Board] contains a bunch of [Piece] positioned in a certain way. */
interface Board {

  /**
   * Returns the [Piece] at a certain [Position] in the board, or null if no [Piece] is localed
   * there.
   *
   * @param position the [Position] for which we're trying to find a [Piece].
   * @return the [Piece] which was found.
   */
  operator fun get(position: Position): Piece?
}
