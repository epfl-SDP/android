package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.BlackKing
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.WhiteKing
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.Black
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White

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
    key: ((PuzzleItem) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues()
) {
  val strings = LocalLocalizedStrings.current

  Scaffold(
      modifier = modifier.padding(contentPadding),
  ) { innerPadding ->
    Column {
      Text(
          text = "Unsolved Puzzles",
          color = MaterialTheme.colors.primary,
          style = MaterialTheme.typography.h4,
          modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp))
      LazyColumn(
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally,
          contentPadding = innerPadding,
      ) {
        items(state.puzzles, key) { puzzle ->
          PuzzleItemListThingy(puzzle, { state.onPuzzleItemClick(puzzle) })
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PuzzleItemListThingy(
    puzzleItem: PuzzleItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val title = "#${puzzleItem.uid} (${puzzleItem.elo})"
  val subtitle = "Playing as ${puzzleItem.playerColor}"

  ListItem(
      modifier = modifier.clickable { onClick() },
      icon = {
        when (puzzleItem.playerColor) {
          White -> Icon(ChessIcons.WhiteKing, null) // TODO: Display the last played piece instead of always king
          Black -> Icon(ChessIcons.BlackKing, null)
        }
      },
      text = { Text(title) },
      secondaryText = { Text(subtitle) },
  )
}
