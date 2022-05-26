package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * The view-model of a visited profile.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 */
@Stable
interface VisitedProfileScreenState<C : ChessMatch, P : PuzzleInfo> : ProfileScreenState<C, P> {

  /** If current user follows the profile */
  val follows: Boolean

  /** A callback invoked when the user follows or unfollows another user. */
  fun onFollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()

  /** Call back function to get back to previous screen */
  fun onBack()
}
