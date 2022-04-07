package ch.epfl.sdp.mobile.ui.play

import ch.epfl.sdp.mobile.ui.social.ChessMatch

/** Interface that represents state of the PlayScreen */
interface PlayScreenState<M : ChessMatch> {

  /* Callable upon actioning button */
  val onNewGameClick: () -> Unit

  /** Action to execute when clicked on match item in list */
  fun onGameItemClick(match: M)

  /** List of matches of current user */
  val matches: List<M>
}
