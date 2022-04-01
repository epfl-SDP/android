package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.state.toColor

/**
 * Composable that represents a list of potential opponents, with one potentially selected
 * @param modifier [Modifier] for this composable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpponentList(
    opponents: List<Profile>,
    state: PrepareGameScreenState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
  LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
      contentPadding = PaddingValues(8.dp),
      state = lazyListState,
  ) {
    val selected = state.selectedOpponent
    if (selected != null) {
      item(key = selected.uid) {
        selected.AsOpponent(
            onClick = { state.selectedOpponent = null },
            selected = true,
            modifier = Modifier.animateItemPlacement(),
        )
      }
    }

    // TODO: When selecting an opponent, do not let the first one in the list be "hidden"
    items(
        items = opponents.filter { it.uid != state.selectedOpponent?.uid },
        key = { it.uid },
        itemContent = {
          it.AsOpponent(
              onClick = { state.selectedOpponent = it },
              modifier = Modifier.animateItemPlacement(),
          )
        },
    )
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
  Button(
      modifier = modifier.fillMaxWidth(),
      shape = CircleShape,
      colors = opponentColor(selected),
      onClick = onClick,
      elevation = null,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
          backgroundColor =
              MaterialTheme.colors
                  .onPrimary
                  .copy(alpha = 0.4f)
                  .compositeOver(MaterialTheme.colors.background))
  else ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)
}
