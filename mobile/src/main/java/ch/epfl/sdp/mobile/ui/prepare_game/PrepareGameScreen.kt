package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

/**
 * Composable that implements a complete PrepareGame screen from [ColorChoiceBar] and
 * [GameTypeChoiceButtons]
 * @param state current state of the screen
 * @param modifier [Modifier] for this composable
 */
@Composable
fun PrepareGameScreen(
    state: PrepareGameScreenState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = modifier.padding(16.dp, 16.dp)) {
    Text(text = strings.prepareGameChooseColor, style = MaterialTheme.typography.subtitle1)
    ColorChoiceBar(
        colorChoice = state.colorChoice,
        onSelectColor = { state.colorChoice = it },
    )
    Text(
        text =
            when (state.gameType) {
              GameType.ONLINE -> strings.prepareGameChooseOpponent
              GameType.LOCAL -> strings.prepareGameChooseGame
            },
        style = MaterialTheme.typography.subtitle1)

    GameTypeChoiceButtons(
        state.gameType,
        {
          state.gameType = GameType.ONLINE
          state.onNewLocalGame
        },
        {
          state.gameType = GameType.LOCAL
          state.onNewLocalGame
        },
        Modifier.align(Alignment.CenterHorizontally))
  }
}
