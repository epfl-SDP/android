package ch.epfl.sdp.mobile.ui.prepare_game

import ch.epfl.sdp.mobile.application.Profile

/**
 * State interface of the [PrepareGameScreen]
 * @property colorChoice chosen color
 * @property selectedOpponent the selected opponent whom to create a game with
 */
interface PrepareGameScreenState {
  var colorChoice: ColorChoice
  var selectedOpponent: Profile?
}

/** Color choices for a chess game */
enum class ColorChoice {
  White,
  Black
}
