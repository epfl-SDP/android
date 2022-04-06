package ch.epfl.sdp.mobile.ui.play

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser

/** Interface that represents state of the PlayScreen */
interface PlayScreenState {

  /** The current [AuthenticatedUser] */
  val user: AuthenticatedUser

  /** Callable upon actioning button for local games */
  fun onLocalGameClick()

  /** Callable upon actioning button for online games */
  fun onOnlineGameClick()

  /* TODO: add necessary attributes: games log... */
}
