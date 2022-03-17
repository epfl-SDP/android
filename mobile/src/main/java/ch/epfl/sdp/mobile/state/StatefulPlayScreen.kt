package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState

/**
 * Player class that holds info about chess player
 * @param newGame Callable action for newGame
 */
/* FIXME: add more useful args : user... */
class Player(private val newGame: () -> Unit) : PlayScreenState {
  /* TODO: override other attributes */
  override val onNewGameClick = newGame
}

/**
 * A stateful implementation of the PlayScreen
 * @param controller Navigation controller to navigate to components
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulPlayScreen(
    controller: NavHostController,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
  /* TODO: Add any necessary args: authenticated user ... */
  val state = remember(controller) { Player(newGame = { controller.navigate(GameRoute) }) }
  PlayScreen(state, modifier.padding(contentPadding))
}
