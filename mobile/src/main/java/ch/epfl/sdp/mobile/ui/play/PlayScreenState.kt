package ch.epfl.sdp.mobile.ui.play

/** Interface that represents state of the PlayScreen */
interface PlayScreenState {

  /* Callable upon actioning button */
  val onNewGameClick: () -> Unit

  /* TODO: add necessary attributes: games log... */
}
