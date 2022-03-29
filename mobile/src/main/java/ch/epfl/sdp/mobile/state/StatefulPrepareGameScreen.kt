package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.prepare_game.ColorChoice
import ch.epfl.sdp.mobile.ui.prepare_game.GameType
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState

/**
 * An implementation of the [PrepareGameScreenState]
 * @param user authenticated user
 * @property onNewLocalGame action for new local games
 * @property onNewOnlineGame action for new online games
 * @property colorChoice chosen color side
 * @property gameType  chosen game type
 *
 */
class PrepareGameScreenStateImpl(
    private val user: AuthenticatedUser,
    override val onNewLocalGame: () -> Unit,
    override val onNewOnlineGame: () -> Unit,
) : PrepareGameScreenState {
  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.WHITE)
  override var gameType: GameType by mutableStateOf(GameType.LOCAL)
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
) {
  val state = remember(user) { PrepareGameScreenStateImpl(user, { /*TODO*/}, { /*TODO*/}) }
  PrepareGameScreen(state, modifier)
}
