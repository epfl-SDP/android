package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
  val state = rememberPrepareGameScreenState(state)

  Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier.padding(paddingValues)) {
    Text(text = strings.pregameChooseColor, style = MaterialTheme.typography.subtitle1)
    ColorChoiceBar(
        colorChoice = state.colorChoice,
        onColorChange = { state.colorChoice = it },
        modifier = modifier)
    Text(
        text =
            when (state.gameType) {
              GameType.ONLINE -> strings.pregameChooseOpponent
              GameType.LOCAL -> strings.pregameChooseGame
            },
        style = MaterialTheme.typography.subtitle1)

    GameTypeChoiceButtons(
        state.gameType,
        { state.gameType = GameType.ONLINE },
        { state.gameType = GameType.LOCAL },
        modifier)
  }
}

class PrepareGameScreenStateImpl(
    private val colorChoiceState: MutableState<ColorChoice>,
    private val gameTypeState: MutableState<GameType>,
    override val onNewLocalGame: () -> Unit,
    override val onNewOnlineGame: () -> Unit
) : PrepareGameScreenState {

  override var colorChoice by colorChoiceState
  override var gameType by gameTypeState
}

@Composable
private fun rememberPrepareGameScreenState(state: PrepareGameScreenState): PrepareGameScreenState {
  val gameType = remember { mutableStateOf(state.gameType) }
  val colorChoice = remember { mutableStateOf(state.colorChoice) }

  return remember {
    PrepareGameScreenStateImpl(colorChoice, gameType, state.onNewLocalGame, state.onNewOnlineGame)
  }
}
