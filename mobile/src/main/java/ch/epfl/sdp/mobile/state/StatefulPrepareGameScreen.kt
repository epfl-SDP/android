package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.prepare_game.ColorChoice
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState

/**
 * An implementation of the [PrepareGameScreenState]
 * @param user authenticated user
 * @property colorChoice chosen color side
 * @property selectedOpponent The selected opponent's [Profile], if any
 */
class PrepareGameScreenStateImpl(
    private val user: AuthenticatedUser,
    override val opponents: List<Profile>,
) : PrepareGameScreenState {
  override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)
  override var selectedOpponent: Profile? by mutableStateOf(null)
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
  val opponents = remember(user) { user.following }.collectAsState(emptyList()).value
  val state = remember(user) { PrepareGameScreenStateImpl(user, opponents) }
  PrepareGameScreen(state, modifier)
}
