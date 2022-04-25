package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.state.ChessMatchAdapter
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/** The view-model of the profile of the currently logged-in user.
 *
 * @param C the type of the [ChessMatch].
 */
@Stable
interface ProfileScreenState <C: ChessMatch> : Person {

  /** The email address of the currently connected user. */
  val email: String

  /** Number of past games */
  val pastGamesCount: Int

  /** List of chess matches */
  val matches: List<C>

  /** On unfollow button clicked */
  fun onUnfollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()

  /**
   * Callback function to open a match.
   *
   * @param match the [ChessMatch] to open.
   */
  fun onMatchClick(match: C)
}
