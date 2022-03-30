package ch.epfl.sdp.mobile.ui.play

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.Loadable
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.SectionSocial
import ch.epfl.sdp.mobile.ui.profile.Match
import ch.epfl.sdp.mobile.ui.profile.chooseSubtitle
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * Composable that composes the PlayScreen [TODO] Contains a new game button only, should be
 * expanded to include history of matches
 * @param state state of the PlayScreen
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun PlayScreen(
    state: PlayScreenState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
  val lazyColumnState = rememberLazyListState()
  val strings = LocalLocalizedStrings.current
  LazyColumn(
      state = lazyColumnState,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
  ) {
    when (state.matches) {
      is Loadable.Loaded<*> -> {
        Log.i("myinfo", "should display")

        items((state.matches as Loadable.Loaded<List<ChessMatch>>).value) { match ->
          val title = strings.profileMatchTitle(match.adversary)
          val subtitle = chooseSubtitle(strings, match.matchResult, match.numberOfMoves)
          Match(title, subtitle, PawniesIcons.SectionSocial)
        }
      }
      is Loadable.Loading -> {
        Log.i("myinfo", "should not display")
      }
    }
  }
  Box(modifier = modifier.fillMaxSize().padding(contentPadding)) {
    NewGameButton(onNewGame = state.onNewGameClick, modifier = Modifier.align(Alignment.BottomEnd))
  }
}

/**
 * Composable that composes a new game button
 * @param onNewGame callable upon button action
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun NewGameButton(onNewGame: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current
  ExtendedFloatingActionButton(
      text = { Text(strings.newGame) },
      onClick = onNewGame,
      shape = CircleShape,
      modifier = modifier.padding(16.dp).height(56.dp),
      backgroundColor = PawniesColors.Green800,
      icon = { Icon(PawniesIcons.Add, null) })
}
