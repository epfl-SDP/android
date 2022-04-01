package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.state.toColor

/**
 * Composable that represents a list of potential opponents, with one potentially selected
 * @param modifier [Modifier] for this composable
 */
@Composable
fun OpponentList(
    opponents: List<Profile>,
    state: PrepareGameScreenState,
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    state.selectedOpponent?.AsOpponent(
        selected = true,
        onClick = { state.selectedOpponent = null },
        modifier = Modifier,
    )
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier, // TODO: How to fill available space properly?
    ) {
      val unselectedOpponents =
          if (state.selectedOpponent == null) {
            opponents
          } else {
            opponents.drop(1)
          }
      unselectedOpponents.map {
        item {
          it.AsOpponent(
              selected = state.selectedOpponent?.uid == it.uid,
              onClick = { state.selectedOpponent = it },
              modifier = Modifier,
          )
        }
      }
    }
  }
}

/**
 * Composable that represents an opponent in [OpponentList]
 * @receiver The profile of the opponent
 * @param selected indicates if the opponent is selected
 * @param onClick button action
 * @param modifier [Modifier] for this composable
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Profile.AsOpponent(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
  Button(
      modifier = modifier.fillMaxWidth(),
      shape = CircleShape,
      colors = opponentColor(selected),
      onClick = onClick,
      elevation =
          ButtonDefaults.elevation(
              defaultElevation = 0.dp,
          ),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
      Box(
          modifier =
              Modifier.size(40.dp)
                  .clip(CircleShape)
                  .background(this@AsOpponent.backgroundColor.toColor()),
      ) { Text(this@AsOpponent.emoji, modifier = Modifier.align(Alignment.Center)) }
      Spacer(modifier = Modifier.padding(8.dp))
      Text(
          this@AsOpponent.name,
          color = MaterialTheme.colors.primaryVariant,
          style = MaterialTheme.typography.subtitle1)
    }
  }
}

/**
 * Returns color of the button given its selection state
 * @param selected True if the button is selected False otherwise
 */
@Composable
private fun opponentColor(
    selected: Boolean,
): ButtonColors {
  return if (selected)
      ButtonDefaults.buttonColors(
          backgroundColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.4f))
  else ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)
}
