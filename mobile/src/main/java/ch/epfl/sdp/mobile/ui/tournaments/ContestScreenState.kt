package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.runtime.Stable

/** The view-model of the list of tournaments screen. */
@Stable
interface ContestScreenState {

  /** The list of completed and ongoing tournaments. */
  val contests: List<Contest>

  /** Callback called when the "New Contest" button is clicked. */
  fun onNewContestClick()
}
