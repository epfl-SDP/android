package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.Filter
import ch.epfl.sdp.mobile.ui.PawniesIcons

@Composable
fun ContestScreen(
    state: ContestScreenState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
  val strings = LocalLocalizedStrings.current

  Scaffold(
      modifier = modifier.padding(contentPadding),
      floatingActionButton = { newContestButton(onClick = state::onNewContestClick) }) {
      innerPadding ->
    Column {
      Row(
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.Top,
          modifier = modifier.padding(start = 16.dp, top = 12.dp, bottom = 16.dp, end = 16.dp)) {
        Text(
            text = strings.tournamentContestsTitle,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4,
            modifier = modifier.padding(end = 163.dp))
        Icon(PawniesIcons.Filter, null)
      }
    }
  }
}

@Composable
fun newContestButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  OutlinedButton(
      onClick = onClick,
      shape = RoundedCornerShape(28.dp),
      contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
      colors = ButtonDefaults.buttonColors(MaterialTheme.colors.onSurface),
      modifier = modifier.width(194.dp).height(56.dp)) {
    Icon(PawniesIcons.Add, null)
    Spacer(Modifier.width(8.dp))
    Text(strings.newContest)
  }
}
