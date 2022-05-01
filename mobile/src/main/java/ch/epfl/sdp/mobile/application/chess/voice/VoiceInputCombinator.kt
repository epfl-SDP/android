package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.parser.Combinators
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.string

/** An object that parse a "perfect" voice input into engine notation */
object VoiceInputCombinator {

  /** A [Parser] which returns the column in a position. */
  private val column = char().filter { it in 'a'..'h' }.map { it - 'a' }

  /** A [Parser] which returns the row in a position. */
  private val row = StringCombinators.digit().map { 8 - it }

  /** A [Parser] which returns a [Position]. */
  private val position =
      column.flatMap { x -> row.map { y -> Position(x, y) } }.filter { it.inBounds }

  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  // TODO : Internationalization
  private val rank =
      Combinators.combine(
          string("king").map { Rank.King },
          string("queen").map { Rank.Queen },
          string("rook").map { Rank.Rook },
          string("bishop").map { Rank.Bishop },
          string("knight").map { Rank.Knight },
          string("pawn").map { Rank.Pawn })

  /** A [Parser] which indicate the action between 2 position */
  private val actionSeparator = string("to")

  /** A [Parser] for a [Move] action. */
  private val move =
      rank
          .flatMap {
            position.flatMap { from ->
              actionSeparator.flatMap { position.map { to -> Move(from, to) } }
            }
          }
          .checkFinished()

  /** Returns a [Parser] for an [Action]. */
  fun action(): Parser<String, Any> = move
}
