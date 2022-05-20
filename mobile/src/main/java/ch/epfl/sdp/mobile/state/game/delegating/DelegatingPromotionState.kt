package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.state.game.core.MutableGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toEngineRank
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.PromotionState

/**
 * An implementation of [MutableGameDelegate] which uses a [MutableGameDelegate] to perform
 * promotion actions.
 *
 * @param delegate the underlying [MutableGameDelegate].
 */
class DelegatingPromotionState(private val delegate: MutableGameDelegate) : PromotionState {

  override var choices: List<ChessBoardState.Rank> by mutableStateOf(emptyList())

  override val confirmEnabled: Boolean
    get() = selection != null

  override var selection: ChessBoardState.Rank? by mutableStateOf(null)
    private set

  /** The start position from the promotion. */
  private var promotionFrom by mutableStateOf(ChessBoardState.Position(0, 0))

  /** The end position from the promotion. */
  private var promotionTo by mutableStateOf(ChessBoardState.Position(0, 0))

  override fun onSelect(rank: ChessBoardState.Rank) {
    selection = if (rank == selection) null else rank
  }

  override fun onConfirm() {
    val rank = selection ?: return
    selection = null
    val action =
        Action.Promote(
            from = Position(promotionFrom.x, promotionFrom.y),
            to = Position(promotionTo.x, promotionTo.y),
            rank = rank.toEngineRank(),
        )
    delegate.tryPerformAction(action)
    choices = emptyList()
  }

  /**
   * Updates the promotion choices.
   *
   * @param from the original promotion position.
   * @param to the final promotion position.
   * @param choices the possible ranks for promotion.
   */
  fun updatePromotion(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
      choices: List<ChessBoardState.Rank>,
  ) {
    this.promotionFrom = from
    this.promotionTo = to
    this.choices = choices
  }
}
