package ch.epfl.sdp.mobile.application.chess.internal

import ch.epfl.sdp.mobile.application.chess.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/** Returns a row with all the pieces of the given color. */
private fun piecesRow(color: Color) =
    persistentListOf(
        Piece(color, Rank.Rook),
        Piece(color, Rank.Knight),
        Piece(color, Rank.Bishop),
        Piece(color, Rank.Queen),
        Piece(color, Rank.King),
        Piece(color, Rank.Bishop),
        Piece(color, Rank.Knight),
        Piece(color, Rank.Rook),
    )

/** Returns a row with only pawns of the given color. */
private fun pawnsRow(color: Color) = List(8) { Piece(color, Rank.Pawn) }.toPersistentList()

/** Returns a row with */
private val EmptyRow = List(8) { null }.toPersistentList()

/** The cells with which a game of chess starts. */
private val DefaultCells =
    listOf(
            piecesRow(Color.Black),
            pawnsRow(Color.Black),
            EmptyRow,
            EmptyRow,
            EmptyRow,
            EmptyRow,
            pawnsRow(Color.White),
            piecesRow(Color.White),
        )
        .toPersistentList()

/**
 * A [PersistentBoard] represents a two-dimensional structure in which pieces can be added and
 * removed.
 *
 * @param cells the cells which are currently present in the board, encoded as a list of rows.
 */
data class PersistentBoard
constructor(
    private val cells: PersistentList<PersistentList<Piece?>>,
) : Board {

  /** A convenience constructor which creates a board with default cells. */
  constructor() : this(DefaultCells)

  override fun get(position: Position): Piece? {
    return cells[position.y][position.x]
  }

  override fun set(position: Position, piece: Piece?): PersistentBoard {
    val row = cells[position.y]
    val updatedRow = row.set(position.x, piece)
    val updatedCells = cells.set(position.y, updatedRow)
    return PersistentBoard(updatedCells)
  }
}
