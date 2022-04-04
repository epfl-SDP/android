package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState.ColorChoice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A composable that makes a [PrepareGameScreen] stateful
 * @param user authenticated user
 * @param modifier [Modifier] for this composable
 */
@Composable
fun StatefulPrepareGameScreen(
    user: AuthenticatedUser,
    navigateToGame: (match: Match) -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val scope = rememberCoroutineScope()
  val chessFacade = LocalChessFacade.current
  val opponents =
      remember(user) { user.following }.collectAsState(emptyList()).value.map { ProfileAdapter(it) }

  val state =
      remember(user, opponents, navigateToGame, onCancelClick, scope, chessFacade) {
        SnapshotPrepareGameScreenState(
            user, opponents, navigateToGame, onCancelClick, scope, chessFacade)
      }
  PrepareGameScreen(state = state, modifier = modifier, key = { it.uid })
}

/**
 * An implementation of the [PrepareGameScreenState]
 * @param user authenticated user
 * @property colorChoice chosen color side
 * @property selectedOpponent The selected opponent's [Profile], if any
 */
class SnapshotPrepareGameScreenState(
    override val user: AuthenticatedUser,
    override val opponents: List<ProfileAdapter>,
    override val navigateToGame: (match: Match) -> Unit,
    override val onCancelClick: () -> Unit,
    override val scope: CoroutineScope,
    override val chessFacade: ChessFacade,
) : PrepareGameScreenState<ProfileAdapter> {

  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)

  override var selectedOpponent: ProfileAdapter? by mutableStateOf(null)

  override val onPlayClick: (ProfileAdapter) -> Unit = {
    scope.launch {
      val (white, black) =
          when (colorChoice) {
            ColorChoice.White -> ProfileAdapter(user) to it
            ColorChoice.Black -> it to ProfileAdapter(user)
          }
      val match = chessFacade.createMatch(white = white.profile, black = black.profile)
      navigateToGame(match)
    }
  }
}
