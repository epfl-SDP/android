package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * The view-model of a visited profile.
 *
 * @param C the type of the [ChessMatch].
 */
@Stable
interface VisitedProfileScreenState<C : ChessMatch> : ProfileScreenState<C> {

  /** If current user follows the profile */
  val follows: Boolean

  /**
   * A callback invoked when the user follows or unfollows another user.
   *
   */
  fun onFollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()
}
