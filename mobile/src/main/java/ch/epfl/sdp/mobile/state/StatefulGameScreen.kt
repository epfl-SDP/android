package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.Move

class ChessGameScreenState(
    private val user: AuthenticatedUser,
    override val moves: List<Move>,
) : GameScreenState {}

data class ChessMove(override val number: Int, override val name: String) : Move

@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val state =
      remember(user) {
        ChessGameScreenState(user, List(4) { (ChessMove(number = 1, name = "Nf3")) })
      }
  GameScreen(state, modifier)
}
