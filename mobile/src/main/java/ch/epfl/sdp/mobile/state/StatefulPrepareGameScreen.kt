package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.prepare_game.ColorChoice
import ch.epfl.sdp.mobile.ui.prepare_game.GameType
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState

class PrepareGameScreenImpl(private val user: AuthenticatedUser,) : PrepareGameScreenState {
    override var colorChoice: ColorChoice = ColorChoice.WHITE
    override var gameType: GameType = GameType.LOCAL

}

@Composable
fun StatefulPrepareGameScreen(user: AuthenticatedUser, modifier: Modifier = Modifier){
    val state = remember (user) { PrepareGameScreenImpl(user)}
    PrepareGameScreen(state, modifier)
}

