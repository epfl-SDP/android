package ch.epfl.sdp.mobile.ui.prepare_game

import ch.epfl.sdp.mobile.ui.social.Person

/**
 * State interface of the [PrepareGameScreen]
 */
interface PrepareGameScreenState<P : Person> {

  /** Color choices for a chess game */
  enum class ColorChoice {
    White,
    Black
  }

  /** The chosen color for the authenticated user */
  var colorChoice: ColorChoice

  /** The list of opponents to display in the [PrepareGameScreen] */
  val opponents: List<P>

  /** A potentially selected opponent to display differently in the opponent list */
  var selectedOpponent: P?

  /**
   * A callback for the action to take when clicking on the "play" button in the dialog, when a
   * specific opponent is selected.
   */
  fun onPlayClick(opponent: P)

  /** A callback for the action to take when clicking on the "cancel" button in the dialog */
  fun onCancelClick()
}
