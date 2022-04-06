package ch.epfl.sdp.mobile.ui.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.profile.Match
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * Composable that composes the PlayScreen [TODO] Contains a new game button only, should be
 * expanded to include history of matches
 * @param state the [PlayScreenState] to manage composable content
 * @param key a function which uniquely identifies the list items.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun <M : ChessMatch> PlayScreen(
    state: PlayScreenState<M>,
    modifier: Modifier = Modifier,
    key: ((M) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
  val strings = LocalLocalizedStrings.current
  Column(modifier) {
    Text(
        text = strings.playOnlineGames,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp))
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier,
    ) { items(state.matches, key) { match -> Match(match, { state.onGameItemClick(match) }) } }
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
