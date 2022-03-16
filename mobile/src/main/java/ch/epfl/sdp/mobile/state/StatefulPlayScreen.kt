package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState

class Player(private val newGame: () -> Unit) : PlayScreenState {
  /*TODO: override other attributes*/
  override val onNewGame = newGame
}

@Composable
fun StatefulPlayScreen(controller: NavHostController, modifier: Modifier = Modifier) {
  val state = remember(controller) { Player(newGame = { controller.navigate(NewGameRoute) }) }
  PlayScreen(state, modifier)
}
