package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Class that holds the necessary info of the [PlayScreen]
 * @param user the current [AuthenticatedUser]
 * @param localGame Callable when new game button is actioned for local games. Creates a local game
 * and switches to it.
 * @param onlineGame Callable when new game button is actioned for online games. Switches to
 * [PrepareGameScreen]
 * @param chessFacade The chess facade to act on the store
 * @param scope The [Composable]'s [CoroutineScope]
 */
class SnapshotPlayScreenState(
    override val user: AuthenticatedUser,
    private val localGame: (match: Match) -> Unit,
    private val onlineGame: () -> Unit,
    private val chessFacade: ChessFacade,
    val scope: CoroutineScope,
) : PlayScreenState {
  override fun onLocalGameClick() {
    scope.launch {
      val match = chessFacade.createLocalMatch(user)
      localGame(match)
    }
  }
  override fun onOnlineGameClick() = this.onlineGame()
}

/**
 * A stateful implementation of the PlayScreen
 * @param navigateToPrepareGame Callable lambda to navigate to [PrepareGameScreen] screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulPlayScreen(
    user: AuthenticatedUser,
    navigateToPrepareGame: () -> Unit,
    navigateToLocalGame: (match: Match) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chess = LocalChessFacade.current
  val scope = rememberCoroutineScope()

  val state =
      remember(user, navigateToLocalGame, navigateToPrepareGame, chess, scope) {
        SnapshotPlayScreenState(
            user = user,
            localGame = navigateToLocalGame,
            onlineGame = navigateToPrepareGame,
            chessFacade = chess,
            scope = scope,
        )
      }
  PlayScreen(state, modifier, contentPadding)
}
