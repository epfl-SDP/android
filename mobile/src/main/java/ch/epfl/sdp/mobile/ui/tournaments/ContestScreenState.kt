package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.runtime.Stable

/** The view-model of the list of tournaments screen. */
@Stable
interface ContestScreenState {

  /** Callback called when the "New Contest" button is clicked. */
  fun onNewContestClick()
}
