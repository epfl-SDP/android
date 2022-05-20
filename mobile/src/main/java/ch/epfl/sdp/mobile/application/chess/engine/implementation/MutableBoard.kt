package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Position

/**
 * An class representing a board of pieces.
 *
 * @param cells the backing [IntArray] with each individual piece.
 */
@JvmInline
value class MutableBoard private constructor(@PublishedApi internal val cells: IntArray) {

  /** Creates a new [MutableBoard]. */
  constructor() : this(IntArray(Size * Size))

  /** Returns true iff the [Position] has a piece. */
  operator fun contains(position: Position): Boolean {
    return !get(position).isNone
  }

  /**
   * Returns the [MutableBoardPiece] at the given [Position] of the board. If you're iterating over the whole
   * chessboard, it's recommended to use [forEachPiece] and [forEachPosition] instead, which are
   * optimized for these use-cases.
   *
   * @param position the [Position] to check for.
   * @return the associated [MutableBoardPiece]. May be [MutableBoardPiece.None].
   */
  operator fun get(position: Position): MutableBoardPiece {
    if (!position.inBounds) return MutableBoardPiece.None
    return MutableBoardPiece.fromPacked(cells[position.x * Size + position.y])
  }

  /**
   * Sets the [MutableBoardPiece] at the given [Position] of the board.
   *
   * @param position the [Position] at which the piece is set.
   * @param piece the [MutableBoardPiece] which is placed.
   */
  operator fun set(position: Position, piece: MutableBoardPiece) {
    if (!position.inBounds) return
    cells[position.x * Size + position.y] = MutableBoardPiece.toInt(piece)
  }

  /**
   * Removes the piece at the given [Position].
   *
   * @param position the [Position] at which the piece is removed.
   */
  fun remove(position: Position) {
    set(position, MutableBoardPiece.None)
  }

  /**
   * Invokes the [block] for each valid [MutableBoardPiece] from the [MutableBoard].
   *
   * @param block the block invoked, with the [Position] and the [MutableBoardPiece].
   */
  inline fun forEachPiece(block: (Position, MutableBoardPiece) -> Unit) {
    forEachPosition { position, piece ->
      if (!piece.isNone) {
        block(position, piece)
      }
    }
  }

  /**
   * Invokes the [block] for each valid [Position] from the [MutableBoard].
   *
   * @param block the block invoked, with the [Position] and the [MutableBoardPiece].
   */
  inline fun forEachPosition(block: (Position, MutableBoardPiece) -> Unit) {
    for (x in AxisBounds) {
      for (y in AxisBounds) {
        val position = Position(x, y)
        block(position, get(position))
      }
    }
  }

  /** Returns a [MutableBoard] that is a copy of the original board. */
  fun copyOf(): MutableBoard {
    return MutableBoard(cells.copyOf())
  }

  companion object {

    /** Returns the size of an axis from a [MutableBoard]. */
    const val Size = 8

    /** Returns the [IntRange] in which the axis bounds are. */
    val AxisBounds = 0 until Size
  }
}
