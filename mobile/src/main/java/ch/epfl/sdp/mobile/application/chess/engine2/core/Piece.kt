package ch.epfl.sdp.mobile.application.chess.engine2.core

import ch.epfl.sdp.mobile.application.chess.engine2.core.ranks.*
import kotlin.experimental.and
import kotlin.experimental.or

private const val PieceColorMask = 0b1.toByte()
private const val PieceColorBlackFlag = 0b0.toByte()
private const val PieceColorWhiteFlag = 0b1.toByte()

private const val PieceRankMask = 0b1110.toByte()
private const val PieceRankBishopFlag = 0b0010.toByte()
private const val PieceRankKingFlag = 0b0100.toByte()
private const val PieceRankKnightFlag = 0b0110.toByte()
private const val PieceRankPawnFlag = 0b1000.toByte()
private const val PieceRankQueenFlag = 0b1010.toByte()
private const val PieceRankRookFlag = 0b1110.toByte()

private const val PieceColorRankFlag = 0b00001111.toByte()
private const val PieceIdFlag = 0b11110000.toByte()

// TODO : Document the flags and this function.
// TODO : Use some dedicated maps.
private fun pack(id: Int, rank: Rank, color: Color): Byte {
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
  val idByte = id.shl(4).toByte()
  return colorByte or rankByte or idByte
}

/**
 * A [Piece] which will be present on a [MutableBoard].
 *
 * Internally, a [Piece] is represented in a single [Byte]. The 8 bits is organized as follows :
 *
 * "iiii-rrr-c"
 *
 * where i stands for a bit dedicated to storing the identifier, r stands for a bit dedicated to
 * storing the rank, and c stands for a bit storing the color. If both the rank and color are
 * zeroes, the piece will be considered as equal to none.
 */
@JvmInline
value class Piece private constructor(private val packed: Byte) {

  constructor(id: Int, rank: Rank, color: Color) : this(pack(id, rank, color))

  private fun hasFlag(mask: Byte, flag: Byte): Boolean = (packed and mask) == flag

  /** Returns true iff this [Piece] represents an absent piece. */
  val isNone: Boolean
    get() = (packed and PieceColorRankFlag) == None.packed

  /**
   * Returns the unique identifier for this piece amongst all the [Piece] of same [Rank] and same
   * [Color] on the board.
   */
  val id: Int
    get() = (packed.toInt() shr 4) and 0xF

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

    /** Unpacks the provided [Byte] and returns the associated [Piece]. */
    fun fromPacked(byte: Byte): Piece {
      return Piece(byte)
    }

    /** Returns the [Byte] representation of the provided [piece]. */
    fun toByte(piece: Piece): Byte {
      return piece.packed
    }

    /** Returns a [Piece] which represents an empty cell. */
    val None = Piece(0b0) // no rank
  }
}
