package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

class PromotionMovableChessBoardState(
    private val user: AuthenticatedUser,
    delegate: GameChessBoardState,
    private val playersInfo: ActualPlayersInfoState,
    private val promotion: ActualPromotionState,
) : AbstractMovableChessBoardState(delegate) {

  override fun tryPerformMove(from: ChessBoardState.Position, to: ChessBoardState.Position) {
    val available = availableActions(from, to)
    val step = game.nextStep as? NextStep.MovePiece ?: return
    val currentPlayingId =
        when (step.turn) {
          Color.Black -> playersInfo.blackProfile?.uid
          Color.White -> playersInfo.whiteProfile?.uid
        }
    if (currentPlayingId == user.uid) {
      if (available.size == 1) {
        game = step.move(available.first())
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
