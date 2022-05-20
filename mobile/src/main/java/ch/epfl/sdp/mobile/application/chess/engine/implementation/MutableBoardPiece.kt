package ch.epfl.sdp.mobile.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.engine.PieceIdentifier
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.Rank.*

/** A [MutableBoardPiece] which will be present on a [MutableBoard]. */
@JvmInline
value class MutableBoardPiece private constructor(private val packed: Int) {

  /**
   * Creates a [MutableBoardPiece].
   *
   * @param id the [PieceIdentifier] for the piece.
   * @param rank the [Rank] for the piece.
   * @param color the [Color] for the piece.
   */
  constructor(id: PieceIdentifier, rank: Rank, color: Color) : this(pack(id, rank, color))

  /** Returns true if the [flag] is contained within the [mask]. */
  private fun hasFlag(mask: Int, flag: Int): Boolean = (packed and mask) == flag

  /** Returns true iff this [MutableBoardPiece] represents an absent piece. */
  val isNone: Boolean
    get() = (packed and PieceColorRankFlag) == None.packed

  /**
   * Returns the unique identifier for this piece amongst all the [MutableBoardPiece] of same [Rank]
   * and same [Color] on the board.
   */
  val id: Int
    get() = (packed ushr 4)

  /** Returns the [Color] associated to this [MutableBoardPiece], if it's not [None]. */
  val color: Color?
    get() =
        when {
          isNone -> null
          hasFlag(mask = PieceColorMask, flag = PieceColorBlackFlag) -> Black
          else -> White
        }

  /** Returns the [Rank] associated to this [MutableBoardPiece], if it's not [None]. */
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

    /** Unpacks the provided [Int] and returns the associated [MutableBoardPiece]. */
    fun fromPacked(byte: Int): MutableBoardPiece {
      return MutableBoardPiece(byte)
    }

    /** Returns the [Int] representation of the provided [piece]. */
    fun toInt(piece: MutableBoardPiece): Int {
      return piece.packed
    }

    /** Returns a [MutableBoardPiece] which represents an empty cell. */
    val None = MutableBoardPiece(0b0) // no rank
  }
}

/** Packs the piece identifier, the [Rank] and the [Color] in an [Int]. */
private fun pack(
    id: PieceIdentifier,
    rank: Rank,
    color: Color,
): Int = requireNotNull(ColorMap[color]) or requireNotNull(RankMap[rank]) or id.value.shl(4)

// Masks and flags for the piece colors.
private const val PieceColorMask = 0b1
private const val PieceColorBlackFlag = 0b0
private const val PieceColorWhiteFlag = 0b1

// Masks and flags for the piece ranks.
private const val PieceRankMask = 0b1110
private const val PieceRankBishopFlag = 0b0010
private const val PieceRankKingFlag = 0b0100
private const val PieceRankKnightFlag = 0b0110
private const val PieceRankPawnFlag = 0b1000
private const val PieceRankQueenFlag = 0b1010
private const val PieceRankRookFlag = 0b1110

// Masks for the piece color and rank.
private const val PieceColorRankFlag = PieceRankMask or PieceColorMask

// Some maps to retrieve the bytes.
private val ColorMap = mapOf(Black to PieceColorBlackFlag, White to PieceColorWhiteFlag)
private val RankMap =
    mapOf(
        Bishop to PieceRankBishopFlag,
        King to PieceRankKingFlag,
        Queen to PieceRankQueenFlag,
        Rook to PieceRankRookFlag,
        Pawn to PieceRankPawnFlag,
        Knight to PieceRankKnightFlag,
    )
