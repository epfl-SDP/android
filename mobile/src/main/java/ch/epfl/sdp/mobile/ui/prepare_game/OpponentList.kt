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
 * @param state the state of the [PrepareGameScreen]
 * @param modifier [Modifier] for this composable
 * @param lazyListState the state of the lazyList displaying opponents
 * @param key a function which uniquely identifies the list items.
 * @param P the type of the [Person].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <P : Person> OpponentList(
    opponents: List<P>,
    selectedOpponent: P?,
    onOpponentClick: (P) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    key: ((P) -> Any)? = null,
) {
  LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
      contentPadding = PaddingValues(8.dp),
      state = lazyListState,
  ) {
    items(
        items = opponents,
        key = key,
        itemContent = { item ->
          Opponent(
              item,
              onClick = { onOpponentClick(item) },
              modifier = Modifier.animateItemPlacement(),
              selected = item == selectedOpponent,
          )
        },
    )
  }
}

/**
 * Composable that represents an opponent in [OpponentList]
 * @param person Person representing the opponent
 * @param onClick button action
 * @param modifier [Modifier] for this composable
 * @param selected indicates if the opponent is selected
 * @param P the type of the [Person].
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
      Spacer(modifier = Modifier.width(16.dp))
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
