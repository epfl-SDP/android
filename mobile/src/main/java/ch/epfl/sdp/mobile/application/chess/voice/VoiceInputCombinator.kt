package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.notation.CommonNotationCombinators
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token

/** An object that parse a "perfect" voice input into engine notation */
object VoiceInputCombinator {
  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  // TODO : Internationalization
  private val rank =
      combine(
          token("king").map { Rank.King },
          token("queen").map { Rank.Queen },
          token("rook").map { Rank.Rook },
          token("bishop").map { Rank.Bishop },
          token("knight").map { Rank.Knight },
          token("pawn").map { Rank.Pawn })

  /** A [Parser] which indicate the action between 2 position */
  private val actionSeparator = token("to")

  /** A [Parser] for a [Move] action. */
  private val move =
      rank
          .flatMap {
            CommonNotationCombinators.position.flatMap { from ->
              actionSeparator.flatMap {
                CommonNotationCombinators.position.map { to -> Move(from, to) }
              }
            }
          }
          .checkFinished()

  /** A [Parser] for a [Promote] action. */
  private val promote =
      CommonNotationCombinators.position // No leading rank because only pawns may be promoted.
          .flatMap { from ->
            actionSeparator.flatMap {
              CommonNotationCombinators.position.flatMap { to ->
                rank.map { rank -> Promote(from, to, rank) }
              }
            }
          }
          .checkFinished()

  /** A [Parser] for an action. */
  private val action = combine(move, promote)

  /** Returns a [Parser] for an [Action]. */
  fun action(): Parser<String, Any> = action
}
