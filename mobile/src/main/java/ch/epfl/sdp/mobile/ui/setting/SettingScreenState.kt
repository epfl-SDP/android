package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState

/** The view-model of the profile of the currently logged-in user. */
@Stable
interface SettingScreenState : ProfileScreenState {

  /** The email address of the logged-in user. */
  val email: String

  /** Number of puzzles */
  val puzzlesCount: Int

  /** On settings button clicked */
  fun onSettingsClick()

  /** On edit profile name button clicked */
  fun onEditProfileNameClick()
}
