package ch.epfl.sdp.mobile.application.chess.engine2.core

/**
 * An class representing a board of pieces.
 *
 * @param cells the backing [ByteArray] with each individual piece.
 */
@JvmInline
value class MutableBoard private constructor(@PublishedApi internal val cells: ByteArray) {

  /** Creates a new [MutableBoard]. */
  constructor() : this(ByteArray(Size * Size))

  /** Returns true iff the [Position] has a piece. */
  operator fun contains(position: Position): Boolean {
    return !get(position).isNone
  }

  /**
   * Returns the [Piece] at the given [Position] of the board. If you're iterating over the whole
   * chessboard, it's recommended to use [forEachPiece] and [forEachPosition] instead, which are
   * optimized for these use-cases.
   *
   * @param position the [Position] to check for.
   * @return the associated [Piece]. May be [Piece.None].
   */
  operator fun get(position: Position): Piece {
    if (!position.inBounds) return Piece.None
    return Piece.fromPacked(cells[position.x * Size + position.y])
  }

  /**
   * Sets the [Piece] at the given [Position] of the board.
   *
   * @param position the [Position] at which the piece is set.
   * @param piece the [Piece] which is placed.
   */
  operator fun set(position: Position, piece: Piece) {
    if (!position.inBounds) return
    cells[position.x * Size + position.y] = Piece.toByte(piece)
  }

  /**
   * Removes the piece at the given [Position].
   *
   * @param position the [Position] at which the piece is removed.
   */
  fun remove(position: Position) {
    set(position, Piece.None)
  }

  /**
   * Invokes the [block] for each valid [Piece] from the [MutableBoard].
   *
   * @param block the block invoked, with the [Position] and the [Piece].
   */
  inline fun forEachPiece(block: (Position, Piece) -> Unit) {
    forEachPosition { position, piece ->
      if (!piece.isNone) {
        block(position, piece)
      }
    }
  }

  /**
   * Invokes the [block] for each valid [Position] from the [MutableBoard].
   *
   * @param block the block invoked, with the [Position] and the [Piece].
   */
  inline fun forEachPosition(block: (Position, Piece) -> Unit) {
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
