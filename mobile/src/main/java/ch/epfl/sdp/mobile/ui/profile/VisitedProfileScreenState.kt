package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable

/** The view-model of a visited profile. */
@Stable
interface VisitedProfileScreenState : ProfileScreenState {
  /** On unfollow button clicked */
  fun onUnfollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()
}
