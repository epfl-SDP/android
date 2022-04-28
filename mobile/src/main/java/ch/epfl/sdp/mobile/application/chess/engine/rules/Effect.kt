package ch.epfl.sdp.mobile.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Position

/**
 * An interface representing a [Effect] which may be performed by a player. Effects may have some
 * effects on a [Board], and will update it according to the move semantics.
 *
 * @param Piece the type of the pieces of the board.
 */
fun interface Effect<Piece : Any> {

  /**
   * Performs a [Effect] and updates a [Board] according to the semantics of this [Effect].
   *
   * @param current the [Board] at the start of the [Effect].
   * @return the updated [Board].
   */
  fun perform(current: Board<Piece>): Board<Piece>

  /** A factory providing access to some functions to build effects on a board. */
  companion object Factory {

    /**
     * Removes the piece at the given [Position] from the board. This might happen if the piece was
     * eaten or replaced.
     *
     * @param P the type of the pieces.
     * @param position the [Position] at which the piece is removed.
     * @return the valid resulting [Effect].
     */
    fun <P : Any> remove(position: Position): Effect<P> = Effect { it.set(position, null) }

    /**
     * Updates the given [Position] by replacing the piece at the given [Position] with the given
     * [Piece]. An existing [Piece] will be removed if it's already present.
     *
     * @param P the type of the pieces.
     * @param position the position at which the [Piece] is put.
     * @param piece the [Piece] which gets placed.
     * @return the valid resulting [Effect].
     */
    fun <P : Any> replace(position: Position, piece: P): Effect<P> = Effect {
      it.set(position, piece)
    }

    /**
     * Applies a sequence of [Effect] in order, from left to right. This may be useful if you want
     * to combine some simple effects into a single action.
     *
     * @param P the type of the pieces.
     * @param effects the sequence of effects that should be applied.
     */
    fun <P : Any> combine(vararg effects: Effect<P>): Effect<P> = Effect {
      effects.fold(it) { board, effect -> effect.perform(board) }
    }

    /**
     * Moves the piece at the given [Position] to the given target. If no piece was present at the
     * starting position or the move didn't stay in the board bounds, the [Effect] will have no
     * effect on the board (since it's not valid).
     *
     * @param P the type of the pieces.
     * @param from the original position of the piece that is moved. The [Piece] must exist.
     * @param target the final position of the piece.
     * @return the valid resulting [Effect].
     */
    fun <P : Any> move(from: Position, target: Position): Effect<P> = Effect {
      val original = it[from] ?: return@Effect it
      if (!target.inBounds) return@Effect it
      replace(target, original).perform(remove<P>(from).perform(it))
    }

    /**
     * Moves the piece at the given [Position] to the given target, and replaces it with the
     * provided piece. If no piece was present at the starting position or the move didn't stay in
     * the board bounds, the [Effect] will have no effect on the board (since it's not valid).
     *
     * @param P the type of the pieces.
     *
     * @param from the original position of the piece that is moved. The [Piece] must exist.
     * @param target the final position of the piece.
     * @param piece the piece to put at the target position.
     * @return the valid resulting [Effect].
     */
    fun <P : Any> promote(from: Position, target: Position, piece: P): Effect<P> =
        combine(
            move(from, target),
            replace(target, piece),
        )
  }
}
