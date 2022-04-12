package ch.epfl.sdp.mobile.ui.play

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.social.ChessMatch

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser

/** Interface that represents state of the PlayScreen */
interface PlayScreenState<M : ChessMatch> {

  /** The current [AuthenticatedUser] */
  val user: AuthenticatedUser

  /** Callable upon actioning button for local games */
  fun onLocalGameClick()

  /** Callable upon actioning button for online games */
  fun onOnlineGameClick()

  /** Action to execute when clicked on match item in list */
  fun onMatchClick(match: M)

  /** List of matches of current user */
  val matches: List<M>
}
