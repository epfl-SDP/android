package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.notation.CommonNotationCombinators.position
import ch.epfl.sdp.mobile.application.chess.notation.CommonNotationCombinators.spaces
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.orElse
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
  private val move = position.flatMap { from -> position.map { to -> Move(from, to) } }

  /** A [Parser] for a [Promote] action. */
  private val promote =
      position.flatMap { from ->
        position.flatMap { to -> promoteRank.map { rank -> Promote(from, to, rank) } }
      }

  /** A [Parser] for an action. */
  private val action = combine(promote, move)

  /** A [Parser] for an arbitrarily long string of actions. */
  private val actions =
      action
          .flatMap { action -> spaces.map { action } }
          .repeat()
          .flatMap { actions -> action.map { actions + it } }
          .orElse { emptyList() }

  /** Returns a [Parser] for a string of actions. */
  fun actions(): Parser<String, List<Action>> = actions.checkFinished()
}
