package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
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
      modifier = modifier.padding(horizontal = 48.dp, vertical = 192.dp),
  )
}
