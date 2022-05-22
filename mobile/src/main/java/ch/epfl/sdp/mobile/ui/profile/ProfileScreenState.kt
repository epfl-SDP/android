package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * The view-model of the profile of the screen.
 *
 * @param C the type of the [ChessMatch].
 */
@Stable
interface ProfileScreenState<C : ChessMatch> : Person {

  /** Number of past games */
  val pastGamesCount: Int

  /** List of chess matches */
  val matches: List<C>

  /**
   * Callback function to open a match.
   *
   * @param match the [ChessMatch] to open.
   */
  fun onMatchClick(match: C)
}
