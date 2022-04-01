package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.ui.prepare_game.ColorChoice
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of the [PrepareGameScreenState]
 * @param user authenticated user
 * @property colorChoice chosen color side
 * @property selectedOpponent The selected opponent's [Profile], if any
 */
class PrepareGameScreenStateImpl(
    override val user: AuthenticatedUser,
    override val opponents: List<Profile>,
    override val navigateToGame: (match: Match) -> Unit,
    override val onCancelClick: () -> Unit,
    override val scope: CoroutineScope,
    override val chessFacade: ChessFacade,
) : PrepareGameScreenState {
  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)
  override var selectedOpponent: Profile? by mutableStateOf(null)

  override val onPlayClick: (Profile) -> Unit = {
    scope.launch {
      val (whiteProfile, blackProfile) =
          when (colorChoice) {
            ColorChoice.White -> user to it
            ColorChoice.Black -> it to user
          }
      val match = chessFacade.createMatch(white = whiteProfile, black = blackProfile)
      navigateToGame(match)
    }
  }
}

/**
 * A composable that makes a [PrepareGameScreen] stateful
 * @param user authenticated user
 * @param modifier [Modifier] for this composable
 */
@Composable
fun StatefulPrepareGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
    navigateToGame: (match: Match) -> Unit,
    onCancelClick: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val chessFacade = LocalChessFacade.current
  val opponents = remember(user) { user.following }.collectAsState(emptyList()).value

  val state =
      remember(user, opponents, navigateToGame, onCancelClick, scope, chessFacade) {
        PrepareGameScreenStateImpl(
            user, opponents, navigateToGame, onCancelClick, scope, chessFacade)
      }
  PrepareGameScreen(state, modifier)
}
