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
 * @property user authenticated user
 * @property colorChoice chosen color side
 * @property gameType chosen game type
 */
class PrepareGameScreenStateImpl(private val user: AuthenticatedUser) : PrepareGameScreenState {
  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)
  override var gameType: GameType by mutableStateOf(GameType.Local)
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
  val state = remember(user) { PrepareGameScreenStateImpl(user) }
  PrepareGameScreen(state, modifier)
}
