package ch.epfl.sdp.mobile.ui.prepare_game

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import kotlinx.coroutines.CoroutineScope

/**
 * State interface of the [PrepareGameScreen]
 * @property colorChoice chosen color
 * @property selectedOpponent the selected opponent whom to create a game with
 */
interface PrepareGameScreenState {
  var colorChoice: ColorChoice
  var selectedOpponent: Profile?
  val opponents: List<Profile>
  val user: AuthenticatedUser
  val navigateToGame: (match: Match) -> Unit
  val onPlayClick: (opponent: Profile) -> Unit
  val onCancelClick: () -> Unit
  val scope: CoroutineScope
  val chessFacade: ChessFacade
}

/** Color choices for a chess game */
enum class ColorChoice {
  White,
  Black
}
