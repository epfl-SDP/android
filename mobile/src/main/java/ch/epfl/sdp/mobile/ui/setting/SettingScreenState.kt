package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.state.ChessMatchAdapter
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * The view-model of the profile of the currently logged-in user.
 *
 * @param C the type of the [ChessMatch].
 */
@Stable
interface SettingScreenState : ProfileScreenState<ChessMatchAdapter> {

  /** The email address of the logged-in user. */
  val email: String

  /** Number of puzzles */
  val puzzlesCount: Int

  /** On settings button clicked */
  fun onSettingsClick()

  /** On edit profile name button clicked */
  fun onEditProfileNameClick()
}
