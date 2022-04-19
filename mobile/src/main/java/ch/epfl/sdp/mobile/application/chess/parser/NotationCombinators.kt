package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.digit

object NotationCombinators {

  fun column(): Parser<String, Int> = char().filter { it in 'a'..'h' }.map { it - 'a' }

  fun row(): Parser<String, Int> = digit().map { 8 - it }

  fun position(): Parser<String, Position> =
      column().flatMap { x -> row().map { y -> Position(x, y) } }.filter { it.inBounds }

  fun rank(): Parser<String, Rank> =
      combine(
              char('K').map { Rank.King },
              char('Q').map { Rank.Queen },
              char('R').map { Rank.Rook },
              char('B').map { Rank.Bishop },
              char('N').map { Rank.Knight },
          )
          .orElse { Rank.Pawn }

  fun moveSeparator(): Parser<String, Char> = char('-') or char('x')

  fun move(): Parser<String, Move> =
      rank().flatMap {
        position().flatMap { from ->
          moveSeparator().flatMap { position().map { to -> Move(from, to) } }
        }
      }

  fun action(): Parser<String, Action> =
      combine(
              move(),
              // TODO : promotion notation
              )
          .checkFinished()
}
