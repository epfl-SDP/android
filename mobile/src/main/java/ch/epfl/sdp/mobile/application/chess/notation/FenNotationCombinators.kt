package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.notation.GenericNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeat
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeatAtLeast
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.charLower
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.digit

/**
 * An object which contains some convenience parser combinators for FEN (Forsyth–Edwards Notation)
 * notation.
 */
object FenNotationCombinators {

  private const val NothingSymbol = '-'
  private const val RowSeparatorSymbol = '/'
  private const val FieldSeparatorSymbol = ' '

  private val LettersToRank =
      mapOf(
          'k' to Rank.King,
          'q' to Rank.Queen,
          'r' to Rank.Rook,
          'b' to Rank.Bishop,
          'n' to Rank.Knight,
          'p' to Rank.Pawn)

  private sealed interface Square {
    val width: Int
    data class Piece(val rank: Rank, val color: Color) : Square {
      override val width = 1
    }
    data class Empty(val count: Int) : Square {
      override val width = count
    }
  }

  private fun piece(): Parser<String, Square.Piece> =
      char().filter { it.lowercaseChar() in LettersToRank.keys }.map {
        val rank = requireNotNull(LettersToRank[it.lowercaseChar()])
        val color = if (it.isUpperCase()) White else Black
        Square.Piece(rank, color)
      }

  private fun empty(): Parser<String, Square.Empty> =
      digit().filter { it in 1..Board.Size }.map { Square.Empty(it) }

  private fun square(): Parser<String, Square> = piece().or(empty())

  private fun delimiter(): Parser<String, Unit> = char(RowSeparatorSymbol).map {}.orElse {}

  private fun lineSquares(): Parser<String, List<Square>> =
      square()
          .repeatAtLeast(count = 1) // Parse at least one square.
          .flatMap { list -> delimiter().map { list } }

  private fun boardSquares(): Parser<String, List<List<Square>>> = lineSquares().repeat()

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

  val spaces = char(FieldSeparatorSymbol).repeatAtLeast(count = 1)

  /** A [Parser] which consumes either a w or b indicating the next playing color */
  val activeColor = char('w').map { White } or char('b').map { Black }

  private val castlingRightsHalf =
      charLower('k').flatMap { charLower('q').map { Pair(first = true, second = true) } } or
          charLower(NothingSymbol).map { Pair(first = false, second = false) } or
          charLower('q').map { Pair(first = false, second = true) } or
          charLower('k').map { Pair(first = true, second = false) }

  val castlingRights =
      castlingRightsHalf.flatMap { white ->
        castlingRightsHalf.map { black ->
          FenNotation.CastlingRights(
              kingSideWhite = white.first,
              queenSideWhite = white.second,
              kingSideBlack = black.first,
              queenSideBlack = black.second,
          )
        }
      }

  /**
   * A [Parser] which consumes a either a nothing symbol or a position representing an en passant
   * move, if allowed
   */
  val enPassant = char(NothingSymbol).map { null } or position.map { it }

  /** A [Parser] which consumes a number of digits representing an integer number */
  val integer = digit().repeat().map { it.joinToString("") }.map { it.toInt() }
}
