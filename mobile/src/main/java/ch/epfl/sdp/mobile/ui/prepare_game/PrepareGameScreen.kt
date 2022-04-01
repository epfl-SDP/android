package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

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
  class SelectedProfileComparator : Comparator<Profile?> {
    override fun compare(a: Profile?, b: Profile?): Int =
        when (state.selectedOpponent?.uid) {
          a?.uid -> -1 // The selected element is "smaller" than every other element
          else -> 0 // Otherwise, order as-is or something
        }
  }

  val strings = LocalLocalizedStrings.current

  Column(modifier = modifier) {
    // TODO: How to space items evenly without doing by hand (and have the divider right next to its
    // neighbours)
    Text(text = strings.prepareGameChooseColor, style = MaterialTheme.typography.subtitle1)
    ColorChoiceBar(
        colorChoice = state.colorChoice,
        onSelectColor = { state.colorChoice = it },
    )

    Text(text = strings.prepareGameChooseOpponent, style = MaterialTheme.typography.subtitle1)
    OpponentList(
        opponents = fakeOpponentList(20).sortedWith(SelectedProfileComparator()),
        state = state,
        modifier = Modifier.weight(1f, fill = true))

    Divider(color = MaterialTheme.colors.onPrimary, thickness = 1.dp)
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.End,
    ) {
      OutlinedButton(
          onClick = { state.onCancelClick() },
          shape = CircleShape,
          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      ) { Text("CANCEL") }
      Spacer(Modifier.padding(8.dp))
      Button(
          onClick = { state.selectedOpponent?.let { state.onPlayClick(it) } },
          shape = CircleShape,
          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
      ) { Text("PLAY !") }
    }
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

private fun fakeOpponentList(n: Int): List<Profile> {
  return List(20) { i ->
    if (i == 0) {
      FakeProfile(
        name = "The real Ronald Weasley",
        uid = "bbRzHbCYs7abfdq8ZjC86WtRvXJ3",
        selected = true)
    } else {
      FakeProfile(name = "Ronald Weasley n° $i", uid = i.toString())
    }
  }
}