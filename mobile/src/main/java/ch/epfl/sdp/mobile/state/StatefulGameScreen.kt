package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState

class ChessGameScreenState(
    private val user: AuthenticatedUser,
) : GameScreenState {}

@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val state = remember(user) { ChessGameScreenState(user) }
  GameScreen(state, modifier)
}
