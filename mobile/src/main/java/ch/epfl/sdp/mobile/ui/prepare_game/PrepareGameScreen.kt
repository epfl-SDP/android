package ch.epfl.sdp.mobile.ui.prepare_game

import android.content.ContentValues
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

@Composable
fun PrepareGameScreen(state: PrepareGameScreenState, modifier: Modifier = Modifier, paddingValues: PaddingValues = PaddingValues()) {
  val strings = LocalLocalizedStrings.current
  Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier.padding(paddingValues)) {
    Text(text = strings.pregameChooseColor, style = MaterialTheme.typography.subtitle1)
    ColorChoiceBar(modifier = modifier)
    Text(text = strings.pregameChooseOpponent, style = MaterialTheme.typography.subtitle1)
  }
}
