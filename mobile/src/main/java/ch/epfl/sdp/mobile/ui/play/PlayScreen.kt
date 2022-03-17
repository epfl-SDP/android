package ch.epfl.sdp.mobile.ui.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

/**
 * Composable that composes the PlayScreen [FIXME] Contains a new game button only, should be
 * expanded
 *
 * @param state state of the PlayScreen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun PlayScreen(
    state: PlayScreenState,
    modifier: Modifier = Modifier,
) {
  Box(contentAlignment = Alignment.BottomEnd, modifier = modifier.fillMaxSize()) {
    NewGameButton(onNewGame = state.onNewGame)
  }
}

/**
 * Composable that composes a new game button
 * @param onNewGame callable upon button action
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun NewGameButton(onNewGame: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  Button(
      onClick = onNewGame,
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
      modifier = modifier.padding(end = 16.dp, bottom = 16.dp)) {
    Icon(Icons.Default.Add, null)
    Spacer(modifier = Modifier.width(8.dp))
    Text(strings.newGame, modifier = modifier)
  }
}
