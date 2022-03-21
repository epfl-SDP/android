package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState

/**
 * Player class that holds info about chess player
 * @param newGame Callable when new game button is actioned. Switches to pregame screen
 */
/* FIXME: add more useful args : user... */
class Player(private val newGame: () -> Unit) : PlayScreenState {
  /* TODO: override other attributes */
  override val onNewGameClick = newGame
}

/**
 * A stateful implementation of the PlayScreen
 * @param navigateToGame Callable lambda to navigate to game screen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulPlayScreen(
    navigateToGame: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  /* TODO: Add any necessary args: authenticated user ... */
  val state = remember(navigateToGame) { Player(newGame = navigateToGame) }
  PlayScreen(state, modifier, contentPadding)
}
