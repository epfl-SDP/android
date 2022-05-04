package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.state.ChessMatchAdapter

/** The view-model of a visited profile. */
@Stable
interface VisitedProfileScreenState : ProfileScreenState<ChessMatchAdapter> {
  /** On unfollow button clicked */
  fun onUnfollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()
}
