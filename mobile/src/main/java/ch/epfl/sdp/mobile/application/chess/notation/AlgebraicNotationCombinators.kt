package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Companion.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Companion.Promote
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.notation.CommonNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.char
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished

/** An object which contains some convenience parser combinators for algebraic notation. */
object AlgebraicNotationCombinators {

  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  private val rank =
      combine(
          char('K').map { Rank.King },
          char('Q').map { Rank.Queen },
          char('R').map { Rank.Rook },
          char('B').map { Rank.Bishop },
          char('N').map { Rank.Knight },
      )

  /** A [Parser] which eats a `-` or an `x` character. */
  private val separator = char('-') or char('x')

  /** A [Parser] for a [Move] action. */
  private val move =
      rank
          .orElse { Rank.Pawn }
          .flatMap {
            position.flatMap { from -> separator.flatMap { position.map { to -> Move(from, to) } } }
          }
          .checkFinished()

  /** A [Parser] for a [Promote] action. */
  private val promote =
      position // No leading rank because only pawns may be promoted.
          .flatMap { from ->
            separator.flatMap {
              position.flatMap { to -> rank.map { rank -> Promote(from, to, rank) } }
            }
          }
          .checkFinished()

  /** A [Parser] for an action. */
  private val action = combine(move, promote)

  /** Returns a [Parser] for an [Action]. */
  fun action(): Parser<String, Action> = action
}
