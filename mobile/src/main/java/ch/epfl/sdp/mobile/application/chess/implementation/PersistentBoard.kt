package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Position
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap

/**
 * Returns an empty [Board] with no pieces.
 *
 * @param Piece the type of the pieces in the board.
 * @return the newly built [Board].
 */
fun <Piece : Any> emptyBoard(): Board<Piece> = buildBoard {}

/**
 * An interface which describes a build for a [PersistentBoard].
 *
 * @param Piece the type of the pieces contained in this board.
 */
interface BoardBuilder<Piece : Any> {

  /** Sets the piece at the given [position] with the provided value. */
  operator fun set(position: Position, value: Piece)
}

private class BoardBuilderImpl<Piece : Any> : BoardBuilder<Piece> {
  val cells = mutableMapOf<Position, Piece>()
  override operator fun set(position: Position, value: Piece) = cells.set(position, value)
}

/**
 * Builds a new [Board] using the provided builder.
 *
 * @param Piece the type of the pieces in the board.
 * @param block the block in which the [Board] is populated.
 * @return the newly built [Board].
 */
fun <Piece : Any> buildBoard(
    block: BoardBuilder<Piece>.() -> Unit,
): Board<Piece> {
  val cells = BoardBuilderImpl<Piece>().apply(block).cells.filter { (pos, _) -> pos.inBounds }
  return PersistentBoard(cells.toPersistentMap())
}

/**
 * An implementation of a [Board] which is backed on a persistent collection.
 *
 * @param Piece the type of the pieces contained in this board.
 */
data class PersistentBoard<Piece : Any>
constructor(
    private val cells: PersistentMap<Position, Piece>,
) : Board<Piece> {

  override fun get(position: Position): Piece? = cells[position]

  override fun set(position: Position, piece: Piece?): PersistentBoard<Piece> {
    if (!position.inBounds) return this
    val updated =
        if (piece != null) {
          cells.put(position, piece)
        } else {
          cells.remove(position)
        }
    return copy(cells = updated)
  }
}
