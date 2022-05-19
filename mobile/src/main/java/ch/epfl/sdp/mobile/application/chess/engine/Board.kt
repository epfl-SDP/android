package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard

/**
 * A [Board] contains a bunch of [Piece] positioned in a certain way.
 *
 * @param Piece the type of the pieces which are present in a board.
 */
interface Board<Piece : Any> : Iterable<Pair<Position, Piece>> {

  /**
   * Returns the [Piece] at a certain [Position] in the board, or null if no [Piece] is localed
   * there.
   *
   * @param position the [Position] for which we're trying to find a [Piece].
   * @return the [Piece] which was found.
   */
  operator fun get(position: Position): Piece?

  /**
   * Sets the given [Position] to the provided value.
   *
   * @param position the [Position] of the board at which the [Piece] is set.
   * @param piece the [Piece] which is set at the given position.
   *
   * @return the updated [Board] with the new [Piece] position.
   */
  fun set(position: Position, piece: Piece?): Board<Piece>

  companion object {

    /** The size of a [Board]. */
    const val Size = 8
  }
}

/**
 * Copies the pieces from this [Board] into a new [Board]. The resulting [Board] is guaranteed to be
 * immutable, and is created by copying all the pieces from the previous board.
 *
 * @param Piece the type of the pieces which are present in a board.
 * @return a new [Board] copy.
 */
fun <Piece : Any> Board<Piece>.toBoard(): Board<Piece> = buildBoard {
  for ((position, piece) in this@toBoard) {
    set(position, piece)
  }
}
