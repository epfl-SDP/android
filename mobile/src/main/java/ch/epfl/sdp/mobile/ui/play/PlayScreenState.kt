package ch.epfl.sdp.mobile.ui.play

import ch.epfl.sdp.mobile.state.Loadable
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/** Interface that represents state of the PlayScreen */
interface PlayScreenState {

  /* Callable upon actioning button */
  val onNewGameClick: () -> Unit

  val matches: Loadable<List<ChessMatch>>
}
