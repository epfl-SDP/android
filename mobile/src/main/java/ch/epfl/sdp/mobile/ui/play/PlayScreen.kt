package ch.epfl.sdp.mobile.ui.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

/**
 * Composable that composes the PlayScreen [TODO] Contains a new game button only, should be
 * expanded to include history of matches
 * @param state state of the PlayScreen
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding The [PaddingValues] to apply to the content
 */
@Composable
fun PlayScreen(
    state: PlayScreenState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val buttonState = rememberExpandableFloatingActionButtonState()

  Box(modifier = modifier.fillMaxSize().padding(contentPadding)) {
    NewGameButton(
        state = buttonState,
        onLocalGame = { state.onLocalGameClick() },
        onRemoteGame = { state.onOnlineGameClick() },
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

  Box(
      modifier.fillMaxSize().background(MaterialTheme.colors.background),
      Alignment.BottomEnd,
  ) {
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
        modifier = Modifier.padding(16.dp),
    )
  }
}
