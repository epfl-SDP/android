package ch.epfl.sdp.mobile.ui.prepare_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Check
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons

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
  ) {
    ChoiceButton(
        text = strings.pregamePlayLocal,
        selected = gameType == GameType.LOCAL,
        onClick = onNewLocalGame,
    )
    Spacer(modifier = modifier.height(16.dp))
    ChoiceButton(
        text = strings.pregamePlayOnline,
        selected = gameType == GameType.ONLINE,
        onClick = onNewOnlineGame,
    )
  }
}

@Composable
fun ChoiceButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

  Button(
      modifier = modifier.width(293.dp),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
      shape = RoundedCornerShape(24.dp),
      colors = buttonColors(selected),
      onClick = onClick) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      if (selected) {
        Icon(PawniesIcons.Check, null)
      }
      Text(text = text)
    }
  }
}

@Composable
private fun buttonColors(selected: Boolean): ButtonColors {
  return if (selected)
      buttonColors(backgroundColor = PawniesColors.Green800, contentColor = PawniesColors.Green500)
  else buttonColors(backgroundColor = Color.Transparent, contentColor = PawniesColors.Green500)
}
