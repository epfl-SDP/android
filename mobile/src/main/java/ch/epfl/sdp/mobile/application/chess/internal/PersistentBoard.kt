package ch.epfl.sdp.mobile.application.chess.internal

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

/** Represents the cells when an empty board is created. */
private val EmptyCells = List(10) { List(10) { null }.toPersistentList() }.toPersistentList()

/**
 * A [PersistentBoard] represents a two-dimensional structure in which pieces can be added and
 * removed.
 *
 * @param cells the cells which are currently present in the board.
 */
data class PersistentBoard
constructor(
    private val cells: PersistentList<PersistentList<Piece?>>,
) : Board {

  /** A convenience constructor which creates a board with empty cells. */
  constructor() : this(EmptyCells)

  override fun get(position: Position): Piece? {
    return cells[position.x][position.y]
  }

  override fun set(position: Position, piece: Piece?): PersistentBoard {
    val row = cells[position.x]
    val updatedRow = row.set(position.y, piece)
    val updatedCells = cells.set(position.x, updatedRow)
    return PersistentBoard(updatedCells)
  }
}
