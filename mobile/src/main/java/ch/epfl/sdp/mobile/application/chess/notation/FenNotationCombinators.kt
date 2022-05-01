package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.digit
import java.util.*

/**
 * An object which contains some convenience parser combinators for FEN (Forsyth–Edwards Notation)
 * notation.
 */
object FenNotationCombinators {

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

  /** A [Parser] which consumes a `/` indicating a new row */
  private val rowSeparator = char('/')

  /** A [Parser] which returns the column in a position. */
  private val column = char().filter { it in 'a'..'h' }.map { it - 'a' }

  /** A [Parser] which returns the row in a position. */
  private val row = digit().map { 8 - it }

  /** A [Parser] which returns a [Position]. */
  private val position =
      column.flatMap { x -> row.map { y -> Position(x, y) } }.filter { it.inBounds }

  /** A [Parser] which consumes a `/` indicating a new row */
  private val fieldSeparator = char(' ')

  /** A [Parser] which consumes a `/` indicating a new row */
  private val activeColor = char('w').map { Color.White } or char('b').map { Color.Black }

  private data class ParserCastlingRights(
      val kingSideWhite: Boolean = false,
      val queenSideWhite: Boolean = false,
      val kingSideBlack: Boolean = false,
      val queenSideBlack: Boolean = false,
  )
}
