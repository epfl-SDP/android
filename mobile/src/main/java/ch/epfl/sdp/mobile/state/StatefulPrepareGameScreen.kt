package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.prepare_game.ColorChoice
import ch.epfl.sdp.mobile.ui.prepare_game.GameType
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState

class PrepareGameScreenStateImpl(
    private val user: AuthenticatedUser,
    override val onNewLocalGame: () -> Unit,
    override val onNewOnlineGame: () -> Unit,
) : PrepareGameScreenState {
  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.WHITE)
  override var gameType: GameType by mutableStateOf(GameType.LOCAL)
}

@Composable
fun StatefulPrepareGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { PrepareGameScreenStateImpl(user, { /*TODO*/}, { /*TODO*/}) }
  PrepareGameScreen(state, modifier)
}
