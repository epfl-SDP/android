package ch.epfl.sdp.mobile.ui.game

import androidx.compose.runtime.Stable

/** An interface representing the possible states of the promotion actions. */
@Stable
interface PromotionState {

  /**
   * The non-empty list of [ChessBoardState.Rank] that should be displayed as available for
   * promotion. If no promotion should be performed, this will be empty.
   */
  val choices: List<ChessBoardState.Rank>

  /** The currently selected [ChessBoardState.Rank] in the promotion dialog. */
  val selection: ChessBoardState.Rank?

  /** True iff the confirm action of promotion is enabled. */
  val confirmEnabled: Boolean

  /**
   * A callback which should be called when the user presses a specific promotion rank.
   *
   * @param rank the rank that was pressed.
   */
  fun onSelect(rank: ChessBoardState.Rank)

  /**
   * A callback which should be called when the user presses the confirm action during promotion.
   */
  fun onConfirm()
}
