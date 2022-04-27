package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings

/**
 * Composable that composes the PlayScreen expanded to include history of matches
 * @param state the [PuzzleSelectionScreenState] to manage composable content
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding The [PaddingValues] to apply to the content
 */
@Composable
fun PuzzleSelectionScreen(
    state: PuzzleSelectionScreenState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
  val strings = LocalLocalizedStrings.current

  Scaffold(
      modifier = modifier.padding(contentPadding),
  ) { innerPadding ->
    Column {
      Text(
          text = "PUZZLES HECK YEAH",
          color = MaterialTheme.colors.primary,
          style = MaterialTheme.typography.h4,
          modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp))
      LazyColumn(
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally,
          contentPadding = innerPadding,
      ) { /* TODO: Add puzzle list here */}
    }
  }
}
