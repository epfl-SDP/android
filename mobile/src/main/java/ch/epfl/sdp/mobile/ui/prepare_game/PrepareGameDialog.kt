package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
        }
      }
      OpponentList(
          opponents = state.opponents,
          state = state,
          modifier = Modifier.animateContentSize(),
          lazyListState = lazyListState,
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
