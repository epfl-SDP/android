package ch.epfl.sdp.mobile.ui.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.LocalPlay
import ch.epfl.sdp.mobile.ui.OnlinePlay
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.profile.Match
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * Composable that composes the PlayScreen expanded to include history of matches
 * @param M the type of the [Match].
 * @param state the [PlayScreenState] to manage composable content
 * @param key a function which uniquely identifies the list items.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding The [PaddingValues] to apply to the content
 */
@Composable
fun <M : ChessMatch> PlayScreen(
    state: PlayScreenState<M>,
    modifier: Modifier = Modifier,
    key: ((M) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
  val strings = LocalLocalizedStrings.current
  val buttonState = rememberExpandableFloatingActionButtonState()

  Column(modifier) {
    Text(
        text = strings.playOnlineGames,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp))
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { items(state.matches, key) { match -> Match(match, { state.onMatchClick(match) }) } }
  }

  Box(
    modifier = modifier.fillMaxSize().padding(contentPadding),
    contentAlignment = Alignment.BottomEnd) {
    NewGameButton(
      state = buttonState,
      onLocalGame = {
        state.onLocalGameClick()
        buttonState.expanded = false
      },
      onRemoteGame = {
        state.onOnlineGameClick()
        buttonState.expanded = false
      },
      modifier = Modifier,
    )
  }
}

/**
 * Composable that composes a new game button
 * @param state the [ExpandableFloatingActionButton]'s state
 * @param onLocalGame callable upon local game button press
 * @param onRemoteGame callable upon remote game button press
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun NewGameButton(
    state: ExpandableFloatingActionButtonState,
    onLocalGame: () -> Unit,
    onRemoteGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current

  ExpandableFloatingActionButton(
      state = state,
      expandedContent = {
        ExpandableFloatingActionButtonItem(
            onClick = onLocalGame,
            icon = { Icon(PawniesIcons.LocalPlay, null) },
            text = { Text(strings.prepareGamePlayLocal) },
            modifier = Modifier.fillMaxWidth(),
        )

        Divider(color = MaterialTheme.colors.onPrimary)

        ExpandableFloatingActionButtonItem(
            onClick = onRemoteGame,
            icon = { Icon(PawniesIcons.OnlinePlay, null) },
            text = { Text(strings.prepareGamePlayOnline) },
            modifier = Modifier.fillMaxWidth(),
        )
      },
      collapsedContent = {
        Icon(PawniesIcons.Add, null)
        Spacer(Modifier.width(16.dp))
        Text(strings.newGame)
      },
      modifier = modifier.padding(16.dp),
  )
}
