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
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * Composable that represents a list of potential opponents, with one potentially selected
 * @param modifier [Modifier] for this composable
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <P : Person> OpponentList(
    state: PrepareGameScreenState<P>,
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
    val selectedOpponent = state.selectedOpponent
    if (selectedOpponent != null) {
      item(key = null /** TODO: Find unique key **/) {
        Opponent(
            selectedOpponent,
            onClick = { state.selectedOpponent = null },
            selected = true,
            modifier = Modifier.animateItemPlacement(),
        )
      }
    }

    items(
        items = state.unselectedOpponents,
        key = null /** { it } TODO: Find unique key **/,
        itemContent = {
          Opponent(
              it,
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
private fun <P : Person> Opponent(
    person: P,
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
              Modifier.size(40.dp).clip(CircleShape).background(person.backgroundColor.toColor()),
      ) { Text(person.emoji, modifier = Modifier.align(Alignment.Center)) }
      Spacer(modifier = Modifier.padding(8.dp))
      Text(
          person.name,
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
