package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.notation.NotationCombinators
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.string

/** An object that parse a "perfect" voice input into engine notation */
object VoiceInputCombinator {
  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  // TODO : Internationalization
  private val rank =
      combine(
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
            NotationCombinators.position.flatMap { from ->
              actionSeparator.flatMap { NotationCombinators.position.map { to -> Move(from, to) } }
            }
          }
          .checkFinished()

  /** A [Parser] for a [Promote] action. */
  private val promote =
      NotationCombinators.position // No leading rank because only pawns may be promoted.
          .flatMap { from ->
            actionSeparator.flatMap {
              NotationCombinators.position.flatMap { to ->
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
