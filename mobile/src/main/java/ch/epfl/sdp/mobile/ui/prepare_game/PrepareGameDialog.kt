package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.social.Person

/**
 * A composable representing a [Dialog] for choosing a configuration of parameters when creating a
 * new online game
 *
 * @param state the [PrepareGameScreenState] that manage the composable contents
 * @param modifier the [Modifier] for the composable
 * @param key a function which uniquely identifies the list items
 * @param P the type of the [Person].
 */
@Composable
fun <P : Person> PrepareGameDialog(
    state: PrepareGameScreenState<P>,
    modifier: Modifier = Modifier,
    key: ((P) -> Any)? = null,
) {
  val strings = LocalLocalizedStrings.current

  Dialog(
      modifier = modifier,
      onCancelClick = { state.onCancelClick() },
      onConfirmClick = { state.onPlayClick() },
      cancelContent = { Text(strings.prepareGameCancel) },
      confirmContent = { Text(strings.prepareGamePlay) },
      confirmEnabled = state.playEnabled,
  ) {
    Column {
      val lazyListState = rememberLazyListState()
      val targetElevation =
          if (lazyListState.firstVisibleItemIndex >= 1 ||
              lazyListState.firstVisibleItemScrollOffset > 0)
              4.dp
          else 0.dp
      val elevation by animateDpAsState(targetElevation)
      Surface(modifier = Modifier.zIndex(1f), elevation = elevation) {
        Column {
          Text(
              text = strings.prepareGameChooseColor,
              style = MaterialTheme.typography.subtitle1,
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
          ColorChoiceBar(
              colorChoice = state.colorChoice,
              onSelectColor = { state.colorChoice = it },
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
          )
          Text(
              text = strings.prepareGameChooseOpponent,
              style = MaterialTheme.typography.subtitle1,
              modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
          )
        }
      }
      OpponentList(
          opponents = state.opponents,
          selectedOpponent = state.selectedOpponent,
          onOpponentClick = state::onOpponentClick,
          modifier = Modifier.animateContentSize(),
          lazyListState = lazyListState,
          key = key,
      )
    }
  }
}
