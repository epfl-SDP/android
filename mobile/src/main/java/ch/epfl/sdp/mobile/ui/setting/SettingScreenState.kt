package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * The view-model of the profile of the currently logged-in user.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 */
@Stable
interface SettingScreenState<C : ChessMatch, P : PuzzleInfo> : ProfileScreenState<C, P> {

  /** The email address of the logged-in user. */
  val email: String

  /** On edit profile image button clicked */
  fun onEditProfileImageClick()

  /** On edit profile name button clicked */
  fun onEditProfileNameClick()
}
