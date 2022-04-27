package ch.epfl.sdp.mobile.ui.setting

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * The view-model of the profile of the currently logged-in user.
 *
 * @param C the type of the [ChessMatch].
 */
@Stable
interface SettingScreenState<C : ChessMatch> : Person {
  /** The email address of the currently connected user. */
  val email: String

  /** Number of past games */
  val pastGamesCount: Int

  /** Number of puzzles */
  val puzzlesCount: Int

  /** List of chess matches */
  val matches: List<C>

  /** On settings button clicked */
  fun onSettingsClick()

  /** On edit button clicked */
  fun onEditClick()

  /**
   * Callback function to open a match.
   *
   * @param match the [ChessMatch] to open.
   */
  fun onMatchClick(match: C)
}
