package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.runtime.Stable

/**
 * The view-model of the list of tournaments screen.
 *
 * @param C the type of the [Contest].
 */
@Stable
interface ContestScreenState<C : Contest> {

  /** The list of completed and ongoing tournaments. */
  val contests: List<C>

  /** Callback called when a Contest is clicked. */
  fun onContestClick(C: Contest)

  /** Callback called when the "New Contest" button is clicked. */
  fun onNewContestClick()
}
