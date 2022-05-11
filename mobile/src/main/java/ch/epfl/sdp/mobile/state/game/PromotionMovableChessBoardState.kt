package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toEnginePosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPlayersInfoState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPromotionState
import ch.epfl.sdp.mobile.state.game.core.MutableGameDelegate
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

class PromotionMovableChessBoardState(
    private val user: AuthenticatedUser,
    private val delegate: MutableGameDelegate,
    private val playersInfo: DelegatingPlayersInfoState,
    private val promotion: DelegatingPromotionState,
) : AbstractMovableChessBoardState(delegate) {

  override fun move(from: ChessBoardState.Position, to: ChessBoardState.Position) {
    val available =
        delegate
            .game
            .actions(from.toEnginePosition())
            .filter { (it.from + it.delta)?.toPosition() == to }
            .toList()
    val step = delegate.game.nextStep as? NextStep.MovePiece ?: return
    val currentPlayingId =
        when (step.turn) {
          Color.Black -> playersInfo.blackProfile?.uid
          Color.White -> playersInfo.whiteProfile?.uid
        }
    if (currentPlayingId == user.uid) {
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
