package ch.epfl.sdp.mobile.ui.prepare_game

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.CoroutineScope

/**
 * State interface of the [PrepareGameScreen]
 * @property colorChoice chosen color
 * @property selectedOpponent the selected opponent whom to create a game with
 */
interface PrepareGameScreenState<P : Person> {

  /** Color choices for a chess game */
  enum class ColorChoice {
    White,
    Black
  }

  var colorChoice: ColorChoice
  val opponents: List<P>
  var selectedOpponent: P?
  val onPlayClick: (P) -> Unit
  val onCancelClick: () -> Unit
}
