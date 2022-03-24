package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
  override var colorChoice: ColorChoice = ColorChoice.WHITE
  override var gameType: GameType = GameType.LOCAL
}

@Composable
fun StatefulPrepareGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues()
) {
  val state = remember(user) { PrepareGameScreenStateImpl(user,{/*TODO*/},{/*TODO*/}) }
  PrepareGameScreen(state, modifier, paddingValues)
}
