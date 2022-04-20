package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.digit

/** An object which contains some convenience parser combinators for chess engine notation. */
object NotationCombinators {

  /** A [Parser] which returns the column in a position. */
  private fun column(): Parser<String, Int> = char().filter { it in 'a'..'h' }.map { it - 'a' }

  /** A [Parser] which returns the row in a position. */
  private fun row(): Parser<String, Int> = digit().map { 8 - it }

  /** A [Parser] which returns a [Position]. */
  private fun position(): Parser<String, Position> =
      column().flatMap { x -> row().map { y -> Position(x, y) } }.filter { it.inBounds }

  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  private fun rank(): Parser<String, Rank> =
      combine(
              char('K').map { Rank.King },
              char('Q').map { Rank.Queen },
              char('R').map { Rank.Rook },
              char('B').map { Rank.Bishop },
              char('N').map { Rank.Knight },
          )
          .orElse { Rank.Pawn }

  /** A [Parser] which eats a `-` or an `x` character. */
  private fun moveSeparator(): Parser<String, Char> = char('-') or char('x')

  /** A [Parser] for a [Move] action. */
  private fun move(): Parser<String, Move> =
      rank()
          .flatMap {
            position().flatMap { from ->
              moveSeparator().flatMap { position().map { to -> Move(from, to) } }
            }
          }
          .checkFinished()

  /** A [Parser] for a [Promote] action. */
  private fun promote(): Parser<String, Promote> =
      position() // No leading rank because only pawns may be promoted.
          .flatMap { from ->
            moveSeparator().flatMap {
              position().flatMap { to -> rank().map { rank -> Promote(from, to, rank) } }
            }
          }
          .checkFinished()

  /** A [Parser] for an [Action]. */
  fun action(): Parser<String, Action> = combine(move(), promote())
}
