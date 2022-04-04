package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.runtime.Stable
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person

/** The view-model of the profile of the currently logged-in user. */
@Stable
interface ProfileScreenState : Person {

  /** The email address of the currently connected user. */
  val email: String

  /** Number of past games */
  val pastGamesCount: Int

  /** List of chess matches */
  var matches: List<ChessMatch>

  /** On unfollow button clicked */
  fun onUnfollowClick()

  /** On challenge button clicked */
  fun onChallengeClick()
}
