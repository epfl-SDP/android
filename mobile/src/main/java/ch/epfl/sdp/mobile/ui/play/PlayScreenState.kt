package ch.epfl.sdp.mobile.ui.play

import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * Interface that represents state of the PlayScreen.
 *
 * @param M the type of the matches.
 */
interface PlayScreenState<M : ChessMatch> {

  /** Callable upon actioning button for local games. */
  fun onLocalGameClick()

  /** Callable upon actioning button for online games. */
  fun onOnlineGameClick()

  /**
   * Action to execute when clicked on match item in list.
   *
   * @param match the clicked match.
   */
  fun onMatchClick(match: M)

  /** List of matches of current user. */
  val matches: List<M>
}
