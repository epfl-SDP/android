package ch.epfl.sdp.mobile.ui.play

/** Interface that represents state of the PlayScreen */
interface PlayScreenState {

  /* Callable upon actioning button */
  val onNewGame: () -> Unit

  /* TODO: add necessary attributes: games log... */
}
