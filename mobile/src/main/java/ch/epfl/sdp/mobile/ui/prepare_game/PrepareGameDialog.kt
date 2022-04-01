package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

@Composable
fun PrepareGameDialog(state: PrepareGameScreenState, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  Dialog(
      modifier = modifier,
      onCancelClick = { state.onCancelClick() },
      onConfirmClick = { state.selectedOpponent?.let { state.onPlayClick(it) } },
      cancelContent = { Text(strings.prepareGameCancel) },
      confirmContent = { Text(strings.prepareGamePlay) },
  ) {
    Column {
      Text(
          text = strings.prepareGameChooseOpponent,
          style = MaterialTheme.typography.subtitle1,
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
      )
      ColorChoiceBar(
          state = state, modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
      Text(
          text = strings.prepareGameChooseOpponent,
          style = MaterialTheme.typography.subtitle1,
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
      )
      OpponentList(state = state)
    }
  }
}

@Composable
fun ColorChoiceBar(state: PrepareGameScreenState, modifier: Modifier = Modifier) {
  ColorChoiceBar(
      colorChoice = state.colorChoice,
      onSelectColor = { state.colorChoice = it },
      modifier = modifier,
  )
}

@Composable
fun OpponentList(state: PrepareGameScreenState, modifier: Modifier = Modifier) {
  OpponentList(
      opponents = state.opponents,
      state = state,
      modifier = modifier,
  )
}
