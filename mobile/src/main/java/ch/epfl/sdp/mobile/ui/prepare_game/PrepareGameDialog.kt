package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

@Composable
fun PrepareGameDialog(state: PrepareGameScreenState, modifier: Modifier = Modifier) {

  val strings = LocalLocalizedStrings.current

  Surface(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
    Column() {
      Text(
          text = strings.prepareGameChooseOpponent,
          style = MaterialTheme.typography.subtitle1,
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
      )
      ColorChoiceBar(state = state, modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

      Text(
          text = strings.prepareGameChooseOpponent,
          style = MaterialTheme.typography.subtitle1,
          modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
      )
      OpponentList(state = state, modifier = Modifier.weight(1f, fill = true))

      Divider(color = MaterialTheme.colors.onPrimary, thickness = 1.dp)

      DialogConfirmButtons(
          strings = strings,
          state = state,
      )
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
      opponents = fakeProfileList(20),
      state = state,
      modifier = modifier,
  )
}

@Composable
fun DialogConfirmButtons(
    strings: LocalizedStrings,
    state: PrepareGameScreenState,
) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.End,
  ) {
    OutlinedButton(
        onClick = { state.onCancelClick() },
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) { Text(strings.prepareGameCancel) }
    Spacer(Modifier.padding(8.dp))
    Button(
        onClick = { state.selectedOpponent?.let { state.onPlayClick(it) } },
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    ) { Text(strings.prepareGamePlay) }
  }
}

private data class FakeProfile(
    override val emoji: String = "🐀",
    override val name: String,
    override val backgroundColor: Profile.Color = Profile.Color.Default,
    override val uid: String,
    override val followed: Boolean = false,
    val selected: Boolean = false,
) : Profile

private fun fakeProfileList(n: Int): List<Profile> {
  return List(n) { i ->
    if (i == 0) {
      FakeProfile(
          name = "The real Ronald Weasley", uid = "bbRzHbCYs7abfdq8ZjC86WtRvXJ3", selected = true)
    } else {
      FakeProfile(name = "Ronald Weasley n° $i", uid = i.toString())
    }
  }
}
