package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Check
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons

/**
 * Composable that implements Local and Online game button choices
 * @param gameType currently chosen game type
 * @param onNewLocalGame call back when new local game button is actioned
 * @param onNewOnlineGame call back when new online game button is actioned
 * @param modifier [Modifier] for this composable
 */
@Composable
fun GameTypeChoiceButtons(
    gameType: GameType,
    onNewOnlineGame: () -> Unit,
    onNewLocalGame: () -> Unit,
    modifier: Modifier = Modifier
) {
  val strings = LocalLocalizedStrings.current
  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
  ) {
    ChoiceButton(
        text = strings.prepareGamePlayLocal,
        selected = gameType == GameType.Local,
        onClick = onNewLocalGame,
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(16.dp))
    ChoiceButton(
        text = strings.prepareGamePlayOnline,
        selected = gameType == GameType.Online,
        onClick = onNewOnlineGame,
        modifier = Modifier.fillMaxWidth())
  }
}

/**
 * Composable that implements a choice button for a game type in [GameTypeChoiceButtons]
 * @param text text to display on button
 * @param selected indicates if the button is selected
 * @param onClick button action
 * @param modifier [Modifier] for this composable
 */
@Composable
fun ChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

  Button(
      modifier = modifier,
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
      shape = RoundedCornerShape(24.dp),
      colors = buttonColors(selected),
      onClick = onClick) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
      if (selected) {
        Icon(PawniesIcons.Check, null)
      }
      Text(text = text)
    }
  }
}

/**
 * Returns colors of the button given its selection state
 * @param selected True if the button is selected False otherwise
 */
@Composable
private fun buttonColors(selected: Boolean): ButtonColors {
  return if (selected)
      buttonColors(backgroundColor = PawniesColors.Green800, contentColor = PawniesColors.Green500)
  else buttonColors(backgroundColor = PawniesColors.Beige050, contentColor = PawniesColors.Green500)
}
