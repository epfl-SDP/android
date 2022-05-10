package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.notation.CommonNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.CastlingRights
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeat
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeatAtLeast
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.digit

/**
 * An object which contains some convenience parser combinators for FEN (Forsyth–Edwards Notation)
 * notation.
 */
object FenNotationCombinators {

  private const val NothingSymbol = '-'
  private const val RowSeparatorSymbol = '/'

  /** Maps FEN letters to [Rank]s for promotion */
  private val LettersToRank =
      mapOf(
          'k' to Rank.King,
          'q' to Rank.Queen,
          'r' to Rank.Rook,
          'b' to Rank.Bishop,
          'n' to Rank.Knight,
          'p' to Rank.Pawn)

  /** Represents either a Piece or a number of Empty squares on a board row */
  private sealed interface Square {
    val width: Int
    data class Piece(val rank: Rank, val color: Color) : Square {
      override val width = 1
    }
    data class Empty(val count: Int) : Square {
      override val width = count
    }
  }

  /** A [Parser] which consumes a piece in FEN and returns the corresponding [Square.Piece] */
  private fun piece(): Parser<String, Square.Piece> =
      char().filter { it.lowercaseChar() in LettersToRank.keys }.map {
        val rank = requireNotNull(LettersToRank[it.lowercaseChar()])
        val color = if (it.isUpperCase()) White else Black
        Square.Piece(rank, color)
      }

  /**
   * A [Parser] which consumes digit in FEN representing empty squares and returns the corresponding
   * [Square.Empty]
   */
  private fun empty(): Parser<String, Square.Empty> =
      digit().filter { it in 1..Board.Size }.map { Square.Empty(it) }

  /**
   * A [Parser] which consumes either a piece or a digit in FEN and returns the corresponding
   * [Square]
   */
  private fun square(): Parser<String, Square> = piece().or(empty())

  /** A [Parser] which consumes a FEN row delimiter */
  private fun delimiter(): Parser<String, Unit> = char(RowSeparatorSymbol).map {}.orElse {}

  /**
   * A [Parser] which consumes an entire FEN row representation, and returns the corresponding
   * [List] of [Square]s
   */
  private fun lineSquares(): Parser<String, List<Square>> =
      square()
          .repeatAtLeast(count = 1) // Parse at least one square.
          .flatMap { list -> delimiter().map { list } }

  /**
   * A [Parser] which consumes a whole board representation in FEN and returns the corresponding
   * [List] of [List]s of [Square]s
   */
  private fun boardSquares(): Parser<String, List<List<Square>>> = lineSquares().repeat()

  /**
   * A [Parser] which consumes a board representation in FEN and returns the corresponding [Board]
   */
  fun board(): Parser<String, Board<Piece<Color>>> =
      boardSquares().map { lines ->
        buildBoard {
          var id = PieceIdentifier(0)
          for ((y, line) in lines.withIndex()) {
            var progress = 0
            for (square in line) {
              if (square is Square.Piece) {
                val position = Position(x = progress, y = y)
                val piece = Piece(square.color, square.rank, id++)
                set(position, piece)
              }
              progress += square.width
            }
          }
        }
      }

  /** A [Parser] which consumes either a w or b indicating the next playing color */
  val activeColor = char('w').map { White } or char('b').map { Black }

  /**
   * Parses the first [Char] of a [String], if it's not empty and has the provided value whether it
   * is lower-case of upper-case.
   *
   * @param value the value that is searched.
   */
  private fun charLower(value: Char): Parser<String, Char> =
      char().filter { it.lowercaseChar() == value }

  /**
   * A [Parser] which consumes a "-" in FEN notation and returns the corresponding [CastlingRights]
   */
  private val noCastlingRights =
      charLower(NothingSymbol).map {
        CastlingRights(
            kingSideWhite = false,
            queenSideWhite = false,
            kingSideBlack = false,
            queenSideBlack = false,
        )
      }

  /**
   * A [Parser] which consumes up to two characters in FEN notation and returns a corresponding
   * [Pair] of rights for white, the first component indicating the kingside rights and the second
   * one representing the queenside rights
   */
  private val castlingWhite =
      char('K').flatMap { char('Q').map { Pair(first = true, second = true) } } or
          char('K').map { Pair(first = true, second = false) } or
          char('Q').map { Pair(first = false, second = true) }

  /**
   * A [Parser] which consumes up to two characters in FEN notation and returns a corresponding
   * [Pair] of rights for black, the first component indicating the kingside rights and the second
   * one representing the queenside rights
   */
  private val castlingBlack =
      char('k').flatMap { char('q').map { Pair(first = true, second = true) } } or
          char('k').map { Pair(first = true, second = false) } or
          char('q').map { Pair(first = false, second = true) }

  /**
   * A [Parser] which consumes up to four characters in FEN notation and returns a corresponding
   * [CastlingRights] for when both white and black have at least one side allowed to castle
   */
  private val bothCastling =
      castlingWhite.flatMap { white ->
        castlingBlack.map { black ->
          CastlingRights(
              kingSideWhite = white.first,
              queenSideWhite = white.second,
              kingSideBlack = black.first,
              queenSideBlack = black.second,
          )
        }
      }

  /**
   * A [Parser] which consumes up to two characters in FEN notation and returns a corresponding
   * [Pair] of rights for when only white has some castling rights
   */
  private val justCastlingWhite =
      castlingWhite.map {
        CastlingRights(
            kingSideWhite = it.first,
            queenSideWhite = it.second,
            kingSideBlack = false,
            queenSideBlack = false,
        )
      }

  /**
   * A [Parser] which consumes up to two characters in FEN notation and returns a corresponding
   * [Pair] of rights for when only black has some castling rights
   */
  private val justCastlingBlack =
      castlingBlack.map {
        CastlingRights(
            kingSideWhite = false,
            queenSideWhite = false,
            kingSideBlack = it.first,
            queenSideBlack = it.second,
        )
      }

  /**
   * A [Parser] which consumes castling rights in FEN notation and returns the corresponding
   * [CastlingRights]
   */
  val castlingRights =
      combine(
          noCastlingRights,
          justCastlingWhite,
          justCastlingBlack,
          bothCastling,
      )

  /**
   * A [Parser] which consumes a either a nothing symbol or a position representing an en passant
   * move, if allowed
   */
  val enPassant = char(NothingSymbol).map { null } or position.map { it }
}
