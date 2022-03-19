package ch.epfl.sdp.mobile.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.*
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/** Transforms the given [Int] to a [PersistentPieceIdentifier]. */
private fun Int.toId(): PieceIdentifier = PersistentPieceIdentifier(this)

/** Returns a row with all the pieces of the given color. */
private fun piecesRow(color: Color) =
    persistentListOf(
        Piece(color, Rank.Rook, 0.toId()),
        Piece(color, Rank.Knight, 0.toId()),
        Piece(color, Rank.Bishop, 0.toId()),
        Piece(color, Rank.Queen, 0.toId()),
        Piece(color, Rank.King, 0.toId()),
        Piece(color, Rank.Bishop, 1.toId()),
        Piece(color, Rank.Knight, 1.toId()),
        Piece(color, Rank.Rook, 1.toId()),
    )

/** Returns a row with only pawns of the given color. */
private fun pawnsRow(color: Color) =
    List(8) { Piece(color, Rank.Pawn, it.toId()) }.toPersistentList()

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
    private val cells: PersistentList<PersistentList<Piece<Color>?>>,
) : Board<Piece<Color>> {

  /** A convenience constructor which creates a board with default cells. */
  constructor() : this(DefaultCells)

  override fun get(position: Position): Piece<Color>? {
    return cells[position.y][position.x]
  }

  override fun set(position: Position, piece: Piece<Color>?): PersistentBoard {
    val row = cells[position.y]
    val updatedRow = row.set(position.x, piece)
    val updatedCells = cells.set(position.y, updatedRow)
    return PersistentBoard(updatedCells)
  }
}
