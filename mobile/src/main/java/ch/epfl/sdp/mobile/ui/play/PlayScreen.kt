package ch.epfl.sdp.mobile.ui.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons

/**
 * Composable that composes the PlayScreen [TODO] Contains a new game button only, should be
 * expanded to include history of matches
 *
 * @param state state of the PlayScreen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun PlayScreen(
    state: PlayScreenState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  Box(
      contentAlignment = Alignment.BottomEnd,
      modifier = modifier.fillMaxSize().padding(contentPadding)) {
    NewGameButton(onNewGame = state.onNewGameClick)
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
  ExtendedFloatingActionButton(
      text = { Text(strings.newGame) },
      onClick = onNewGame,
      shape = CircleShape,
      modifier = modifier.padding(16.dp).size(169.dp, 56.dp),
      backgroundColor = PawniesColors.Green800,
      icon = { Icon(PawniesIcons.Add, null) })
}
