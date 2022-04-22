package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/** The view-model of the profile of the currently logged-in user. */
@Stable
interface SettingScreenState : Person {
  /** The email address of the currently connected user. */
  val email: String

  /** Number of past games */
  val pastGamesCount: Int

  /** Number of puzzles */
  val puzzlesCount: Int

  /** List of chess matches */
  val matches: List<ChessMatch>

  /** On settings button clicked */
  fun onEditProfileImageClick()

  /** On edit profile name button clicked */
  fun onEditProfileNameClick()
}
