package ch.epfl.sdp.mobile.ui.play

import ch.epfl.sdp.mobile.ui.social.ChessMatch

/** Interface that represents state of the PlayScreen */
interface PlayScreenState {

  /* Callable upon actioning button */
  val onNewGameClick: () -> Unit

  /** List of matches of current user */
  val matches: List<ChessMatch>
}
