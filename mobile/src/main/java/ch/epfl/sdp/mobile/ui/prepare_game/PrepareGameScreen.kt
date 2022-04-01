package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable that implements a complete PrepareGame screen
 * @param state current state of the screen
 * @param modifier [Modifier] for this composable
 */
@Composable
fun PrepareGameScreen(
    state: PrepareGameScreenState,
    modifier: Modifier = Modifier,
) {
  PrepareGameDialog(
      state = state,
      modifier = modifier.padding(vertical = 48.dp),
  )
}
