package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.runtime.Stable

/**
 * The view-model of the list of tournaments screen.
 *
 * @param C the type of the [ContestInfo].
 */
@Stable
interface ContestScreenState<C : ContestInfo> {

  /** The list of completed and ongoing tournaments. */
  val contests: List<C>

  /**
   * Callback called when a Contest is clicked.
   *
   * @param contest the contest which was clicked.
   */
  fun onContestClick(contest: C)

  /** Callback called when the "New Contest" button is clicked. */
  fun onNewContestClick()

  /** Callback called when the filter action is clicked. */
  fun onFilterClick()
}
