package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * The view-model of a visited profile.
 *
 * @param C the type of the [ChessMatch].
 */
@Stable
interface VisitedProfileScreenState<C : ChessMatch> : ProfileScreenState<C> {
  /** On unfollow button clicked */
  fun onUnfollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()
}
