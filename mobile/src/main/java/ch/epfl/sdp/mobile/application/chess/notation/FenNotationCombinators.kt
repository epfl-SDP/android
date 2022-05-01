package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.FieldSeparatorSymbol
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.nothingSymbol
import ch.epfl.sdp.mobile.application.chess.notation.GenericNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeatAtLeast
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.charLower
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token

/**
 * An object which contains some convenience parser combinators for FEN (Forsyth–Edwards Notation)
 * notation.
 */
object FenNotationCombinators {

  val spaces = char(FieldSeparatorSymbol).repeatAtLeast(count = 1)

  /** A [Parser] which consumes either a w or b indicating the next playing color */
  val activeColor = char('w').map { White } or char('b').map { Black }

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

  /**
   * A [Parser] which consumes a either a nothing symbol or a position representing an en passant
   * move, if allowed
   */
  val enPassant = char(nothingSymbol).map { null } or position.map { it }

  /** A [Parser] which consumes a token representing an integer number */
  val integer = token(FieldSeparatorSymbol.toString()).map { it.toInt() }
}
