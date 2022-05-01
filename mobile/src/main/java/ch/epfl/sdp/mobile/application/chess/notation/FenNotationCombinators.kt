package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.FieldSeparatorSymbol
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.RowSeparatorSymbol
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.nothingSymbol
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
import java.util.*

/**
 * An object which contains some convenience parser combinators for FEN (Forsyth–Edwards Notation)
 * notation.
 */
object FenNotationCombinators {

  /** A [Parser] which returns the a number of empty squares or the [Rank] and [Color] of a piece */
  val piece =
      combine(
          char('K').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.White, Rank.King))) },
          char('Q').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.White, Rank.Queen))) },
          char('R').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.White, Rank.Rook))) },
          char('B').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.White, Rank.Bishop))) },
          char('N').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.White, Rank.Knight))) },
          char('P').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.White, Rank.Pawn))) },
          char('k').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.Black, Rank.King))) },
          char('q').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.Black, Rank.Queen))) },
          char('r').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.Black, Rank.Rook))) },
          char('b').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.Black, Rank.Bishop))) },
          char('n').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.Black, Rank.Knight))) },
          char('p').map { FenNotation.ParserPiece(1, Optional.of(Pair(Color.Black, Rank.Pawn))) },
          digit().map { FenNotation.ParserPiece(it, Optional.empty()) }
      )

  /** A [Parser] which consumes either a w or b indicating the next playing color */
  val activeColor = char('w').map { Color.White } or char('b').map { Color.Black }

  private val castlingRightsHalf =
      charLower('k').flatMap { charLower('q').map { Pair(first = true, second = true) } } or
          charLower(nothingSymbol).map { Pair(first = false, second = false) } or
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

  /** A [Parser] which consumes a either a nothing symbol or a position representing an en passant move, if allowed */
  private val enPassant =
      char(nothingSymbol).map { null } or position.map { it }

  /** A [Parser] which consumes a token representing an integer number */
  private val integer = token(FieldSeparatorSymbol.toString()).map { it.toInt() }
}
