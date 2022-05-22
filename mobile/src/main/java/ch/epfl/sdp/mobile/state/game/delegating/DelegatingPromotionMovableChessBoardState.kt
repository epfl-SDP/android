package ch.epfl.sdp.mobile.state.game.delegating

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.state.game.AbstractMovableChessBoardState
import ch.epfl.sdp.mobile.state.game.core.MutableGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toEnginePosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/**
 * An implementation of [AbstractMovableChessBoardState] which uses the [DelegatingPromotionState]
 * to trigger the promotion dialog, and the [DelegatingPlayersInfoState] to check whose turn it is
 * to play.
 *
 * @param user the current [AuthenticatedUser].
 * @param delegate the underlying [MutableGameDelegate].
 * @param playersInfo the [DelegatingPlayersInfoState], providing access to the player info.
 * @param promotion the [DelegatingPromotionState], providing access to the promotion info.
 */
class DelegatingPromotionMovableChessBoardState(
    private val user: AuthenticatedUser,
    private val delegate: MutableGameDelegate,
    private val playersInfo: DelegatingPlayersInfoState,
    private val promotion: DelegatingPromotionState,
) : AbstractMovableChessBoardState(delegate) {

  /** Returns the color of the user, if they're participating to the game. */
  override val rotatedBoard: Boolean
    get() =
        when (user.uid) {
          // The order matters, since we want the board NOT to be rotated if the user is playing a
          // local game against themselves !
          playersInfo.whiteProfile?.uid -> false
          playersInfo.blackProfile?.uid -> true
          else -> false
        }

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
