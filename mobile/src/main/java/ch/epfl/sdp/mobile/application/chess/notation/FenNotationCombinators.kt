package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.notation.GenericNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.charLower
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.digit
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token

/**
 * An object which contains some convenience parser combinators for FEN (Forsyth–Edwards Notation)
 * notation.
 */
object FenNotationCombinators {

  private const val nothingSymbol = '-'
  private const val RowSeparatorSymbol = '/'
  private const val FieldSeparatorSymbol = ' '

  private data class ParserPiece(val color: Color, val rank: Rank)

  private fun ParserPiece.toEnginePiece(id: PieceIdentifier): Piece<Color> {
    return Piece(color, rank, id)
  }

  /** A [Parser] which consumes an integer number representing empty squares */
  private val empty = digit()

  /** A [Parser] which returns the [Rank] and [Color] of a piece */
  private val piece =
      combine(
          char('K').map { ParserPiece(Color.White, Rank.King) },
          char('Q').map { ParserPiece(Color.White, Rank.Queen) },
          char('R').map { ParserPiece(Color.White, Rank.Rook) },
          char('B').map { ParserPiece(Color.White, Rank.Bishop) },
          char('N').map { ParserPiece(Color.White, Rank.Knight) },
          char('P').map { ParserPiece(Color.White, Rank.Pawn) },
          char('k').map { ParserPiece(Color.Black, Rank.King) },
          char('q').map { ParserPiece(Color.Black, Rank.Queen) },
          char('r').map { ParserPiece(Color.Black, Rank.Rook) },
          char('b').map { ParserPiece(Color.Black, Rank.Bishop) },
          char('n').map { ParserPiece(Color.Black, Rank.Knight) },
          char('p').map { ParserPiece(Color.Black, Rank.Pawn) },
      )

  /** A [Parser] which consumes a row separator indicating a new row */
  private val rowSeparator = char(RowSeparatorSymbol)

  /** A [Parser] which consumes a field separator indicating a new row */
  private val fieldSeparator = char(FieldSeparatorSymbol)

  /** A [Parser] which consumes either a w or b indicating the next playing color */
  private val activeColor = char('w').map { Color.White } or char('b').map { Color.Black }

  private data class ParserCastlingRights(
      val kingSideWhite: Boolean = false,
      val queenSideWhite: Boolean = false,
      val kingSideBlack: Boolean = false,
      val queenSideBlack: Boolean = false,
  )

  private val castlingRightsHalf =
      charLower('k').flatMap { charLower('q').map { Pair(first = true, second = true) } } or
          charLower(nothingSymbol).map { Pair(first = false, second = false) } or
          charLower('q').map { Pair(first = false, second = true) } or
          charLower('k').map { Pair(first = true, second = false) }

  private val castlingRights =
      castlingRightsHalf.flatMap { white ->
        castlingRightsHalf.map { black ->
          ParserCastlingRights(
              kingSideWhite = white.first,
              queenSideWhite = white.second,
              kingSideBlack = black.first,
              queenSideBlack = black.second,
          )
        }
      }

  /** A [Parser] which consumes a either a nothing symbol or a position representing an en passant move, if allowed */
  private val enPassant =
      char(nothingSymbol).map { null } or position.map { it }

  /** A [Parser] which consumes a token representing an integer number */
  private val integer = token(delimiter = FieldSeparatorSymbol.toString()).map { it.toInt() }
}
