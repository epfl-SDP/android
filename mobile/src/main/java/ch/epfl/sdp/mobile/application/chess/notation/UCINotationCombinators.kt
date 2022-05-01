package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.notation.GenericNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.or
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeat
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished

/** An object which contains some convenience parser combinators for algebraic notation. */
object UCINotationCombinators {

  /** A [Parser] which returns the [Rank] of a piece for promotion */
  private val promoteRank =
      combine(
          StringCombinators.char('q').map { Rank.Queen },
          StringCombinators.char('r').map { Rank.Rook },
          StringCombinators.char('b').map { Rank.Bishop },
          StringCombinators.char('n').map { Rank.Knight },
      )

  /** A [Parser] for a [Move] action. */
  private val move =
      position.flatMap { from -> position.map { to -> Move(from, to) } }.checkFinished()

  /** A [Parser] for a [Promote] action. */
  private val promote =
      position
          .flatMap { from ->
            position.flatMap { to -> promoteRank.map { rank -> Promote(from, to, rank) } }
          }
          .checkFinished()

  /** A [Parser] for an action. */
  private val action = combine(move, promote)

  /** A [Parser] for an arbitrarily long sequence of actions */
  private val actions = action.repeat()

  /** Returns a [Parser] for a list of [Action]. */
  fun uciActions(): Parser<String, List<Action>> = actions
}
