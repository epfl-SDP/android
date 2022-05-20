package ch.epfl.sdp.mobile.state.game.delegating

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.state.game.AbstractMovableChessBoardState
import ch.epfl.sdp.mobile.state.game.core.MutableGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toEngineColor
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toEnginePosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/**
 * An implementation of [AbstractMovableChessBoardState] which uses the [DelegatingPromotionState]
 * to trigger the promotion dialog, and the [DelegatingPuzzleInfoState] to check whose turn it is to
 * play.
 *
 * @param user the current [AuthenticatedUser].
 * @param delegate the underlying [MutableGameDelegate].
 * @param puzzleState the [DelegatingPuzzleInfoState], providing access to the puzzle's state info.
 * @param promotion the [DelegatingPromotionState], providing access to the promotion info.
 */
class DelegatingPuzzlePromotionMovableChessBoardState(
    private val user: AuthenticatedUser,
    private val delegate: MutableGameDelegate,
    private val puzzleState: DelegatingPuzzleInfoState,
    private val promotion: DelegatingPromotionState,
) : AbstractMovableChessBoardState(delegate) {

  override fun move(from: ChessBoardState.Position, to: ChessBoardState.Position) {
    val available =
        delegate
            .game
            .actions(from.toEnginePosition())
            .filter { (it.from + it.delta).toPosition() == to }
            .toList()
    val step = delegate.game.nextStep as? NextStep.MovePiece ?: return
    val userCurrentlyPlaying = step.turn == puzzleState.puzzleInfo.playerColor.toEngineColor()

    if (userCurrentlyPlaying && puzzleState.currentMoveNumber < puzzleState.expectedMoves) {
      if (available.size == 1) {
        delegate.tryPerformAction(available.first())
      } else {
        promotion.updatePromotion(
            from = from,
            to = to,
            choices = available.filterIsInstance<Action.Promote>().map { it.rank.toRank() },
        )
      }
    }
  }
}
