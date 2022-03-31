package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
  Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = modifier.padding(16.dp, 16.dp)) {
    Text(text = strings.prepareGameChooseColor, style = MaterialTheme.typography.subtitle1)
    ColorChoiceBar(
        colorChoice = state.colorChoice,
        onSelectColor = { state.colorChoice = it },
    )
    Text(text = strings.prepareGameChooseOpponent, style = MaterialTheme.typography.subtitle1)

    OpponentList(
        opponents = state.opponents.sortedWith(SelectedProfileComparator()),
        state = state,
    )
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
