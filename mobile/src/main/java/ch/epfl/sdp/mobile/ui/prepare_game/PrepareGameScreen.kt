package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesTheme

@Preview(showBackground = true, backgroundColor = 4294966246)
@Composable
fun preview_home() {
  val state =
      object : PrepareGameScreenState {
        override var colorChoice: ColorChoice = ColorChoice.WHITE
        override var gameType: GameType = GameType.LOCAL
        override val onNewLocalGame: () -> Unit = {}
        override val onNewOnlineGame: () -> Unit = {}
      }
  PawniesTheme { PrepareGameScreen(state, Modifier.padding(16.dp, 16.dp)) }
}

@Composable
fun PrepareGameScreen(
    state: PrepareGameScreenState,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues()
) {
  val strings = LocalLocalizedStrings.current
  var colorChoice by remember { mutableStateOf(state.colorChoice) }
  var gameType by remember { mutableStateOf(state.gameType) }
  Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = modifier.padding(paddingValues)) {
    Text(text = strings.pregameChooseColor, style = MaterialTheme.typography.subtitle1)
    ColorChoiceBar(
        colorChoice = colorChoice,
        onColorChange = { colorChoice = it },
    )
    Text(
        text =
            when (gameType) {
              GameType.ONLINE -> strings.pregameChooseOpponent
              GameType.LOCAL -> strings.pregameChooseGame
            },
        style = MaterialTheme.typography.subtitle1)

    GameTypeChoiceButtons(
        gameType,
        {
          gameType = GameType.ONLINE
          state.onNewLocalGame
        },
        {
          gameType = GameType.LOCAL
          state.onNewLocalGame
        },
        Modifier.align(Alignment.CenterHorizontally))
  }
}
