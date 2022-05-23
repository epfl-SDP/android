package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Position
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
 * An interface which describes a builder for a [Board].
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
 * An implementation of a [Board] which is backed by a persistent collection.
 *
 * @param Piece the type of the pieces contained in this board.
 * @param cells the map of the positions and their contained cells.
 */
private data class PersistentBoard<Piece : Any>
constructor(
    private val cells: PersistentMap<Position, Piece>,
) : Board<Piece>, Iterable<Pair<Position, Piece>> by cells.map({ (a, b) -> Pair(a, b) }) {

  override fun get(position: Position): Piece? = cells[position]
}
