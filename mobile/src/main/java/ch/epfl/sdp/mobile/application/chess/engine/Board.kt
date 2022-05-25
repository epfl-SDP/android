package ch.epfl.sdp.mobile.application.chess.engine

/**
 * A [Board] contains a bunch of [Piece] positioned in a certain way.
 *
 * @param Piece the type of the pieces which are present in a board.
 */
interface Board<out Piece : Any> : Iterable<Pair<Position, Piece>> {

  /**
   * Returns the [Piece] at a certain [Position] in the board, or null if no [Piece] is localed
   * there.
   *
   * @param position the [Position] for which we're trying to find a [Piece].
   * @return the [Piece] which was found.
   */
  operator fun get(position: Position): Piece?

  companion object {

    /** The size of a [Board]. */
    const val Size = 8
  }
}
