package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine2.core.ranks.*

private const val PieceColorMask = 0b1
private const val PieceColorBlackFlag = 0b0
private const val PieceColorWhiteFlag = 0b1

private const val PieceRankMask = 0b1110
private const val PieceRankBishopFlag = 0b0010
private const val PieceRankKingFlag = 0b0100
private const val PieceRankKnightFlag = 0b0110
private const val PieceRankPawnFlag = 0b1000
private const val PieceRankQueenFlag = 0b1010
private const val PieceRankRookFlag = 0b1110

private const val PieceColorRankFlag = 0b00001111
private const val PieceIdFlag = 0b11110000

// TODO : Document the flags and this function.
// TODO : Use some dedicated maps.
private fun pack(id: Int, rank: Rank, color: Color): Int {
  val colorByte =
      when (color) {
        Color.Black -> PieceColorBlackFlag
        Color.White -> PieceColorWhiteFlag
      }
  val rankByte =
      when (rank) {
        is Bishop -> PieceRankBishopFlag
        is King -> PieceRankKingFlag
        is Queen -> PieceRankQueenFlag
        is Rook -> PieceRankRookFlag
        is Pawn -> PieceRankPawnFlag
        is Knight -> PieceRankKnightFlag
        else -> 0
      }
  val idByte = id.shl(4)
  return colorByte or rankByte or idByte
}

/**
 * A [Piece] which will be present on a [MutableBoard].
 *
 * Internally, a [Piece] is represented in a single [Int].
 */
@JvmInline
value class Piece private constructor(private val packed: Int) {

  constructor(id: Int, rank: Rank, color: Color) : this(pack(id, rank, color))

  private fun hasFlag(mask: Int, flag: Int): Boolean = (packed and mask) == flag

  /** Returns true iff this [Piece] represents an absent piece. */
  val isNone: Boolean
    get() = (packed and PieceColorRankFlag) == None.packed

  /**
   * Returns the unique identifier for this piece amongst all the [Piece] of same [Rank] and same
   * [Color] on the board.
   */
  val id: Int
    get() = (packed ushr 4)

  /** Returns the [Color] associated to this [Piece], if it's not [None]. */
  val color: Color?
    get() =
        when {
          isNone -> null
          hasFlag(mask = PieceColorMask, flag = PieceColorBlackFlag) -> Color.Black
          else -> Color.White
        }

  /** Returns the [Rank] associated to this [Piece], if it's not [None]. */
  val rank: Rank?
    get() =
        when {
          hasFlag(mask = PieceRankMask, flag = PieceRankBishopFlag) -> Bishop
          hasFlag(mask = PieceRankMask, flag = PieceRankKingFlag) -> King
          hasFlag(mask = PieceRankMask, flag = PieceRankKnightFlag) -> Knight
          hasFlag(mask = PieceRankMask, flag = PieceRankPawnFlag) -> Pawn
          hasFlag(mask = PieceRankMask, flag = PieceRankQueenFlag) -> Queen
          hasFlag(mask = PieceRankMask, flag = PieceRankRookFlag) -> Rook
          else -> null
        }

  companion object {

    /** Unpacks the provided [Int] and returns the associated [Piece]. */
    fun fromPacked(byte: Int): Piece {
      return Piece(byte)
    }

    /** Returns the [Int] representation of the provided [piece]. */
    fun toInt(piece: Piece): Int {
      return piece.packed
    }

    /** Returns a [Piece] which represents an empty cell. */
    val None = Piece(0b0) // no rank
  }
}
