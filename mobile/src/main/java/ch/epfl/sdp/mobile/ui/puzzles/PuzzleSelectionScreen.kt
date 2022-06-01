package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.state.PuzzleInfoAdapter

/**
 * Composable that composes the PlayScreen expanded to include history of matches.
 * @param state the [PuzzleSelectionScreenState] to manage composable content.
 * @param modifier the [Modifier] for this composable.
 * @param key the key to uniquely identify a [PuzzleInfo] list item.
 * @param contentPadding The [PaddingValues] to apply to the content.
 */
@Composable
fun PuzzleSelectionScreen(
    state: PuzzleSelectionScreenState<PuzzleInfoAdapter>,
    modifier: Modifier = Modifier,
    key: ((PuzzleInfo) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues()
) {

  val strings = LocalLocalizedStrings.current
  val lazyListState = rememberLazyListState()
  val targetShadow =
      if (lazyListState.firstVisibleItemIndex >= 1 ||
          lazyListState.firstVisibleItemScrollOffset > 0)
          4.dp
      else 0.dp
  val shadow by animateDpAsState(targetShadow)

  Scaffold(
      topBar = {
        Text(
            text = strings.puzzleUnsolvedPuzzles,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4,
            modifier =
                Modifier.shadow(shadow)
                    .background(MaterialTheme.colors.background)
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        )
      },
      modifier = modifier.padding(contentPadding),
  ) { innerPadding ->
    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
        contentPadding = innerPadding,
        modifier =
            Modifier.semantics { contentDescription = strings.puzzleListContentDescription }) {
      items(state.puzzles, key) { puzzle ->
        PuzzleListInfo(puzzle, onClick = { state.onPuzzleItemClick(puzzle) })
      }
    }
  }
}

/**
 * Displays a [PuzzleSelectionScreen]'s list item.
 *
 * @param puzzleInfo The information about the puzzle to display.
 * @param onClick Action to execute when clicked on puzzle item in list
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PuzzleListInfo(
    puzzleInfo: PuzzleInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val typography = MaterialTheme.typography
  val colors = MaterialTheme.colors

  ListItem(
      modifier = modifier.clickable { onClick() },
      icon = puzzleInfo.icon,
      text = {
        Text(
            text =
                buildAnnotatedString {
                  withStyle(
                      style =
                          typography.subtitle1.copy(color = colors.primaryVariant).toSpanStyle()) {
                    append("#${puzzleInfo.uid} ")
                  }
                  withStyle(style = typography.body2.copy(color = colors.primary).toSpanStyle()) {
                    append("(${puzzleInfo.elo})")
                  }
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
      },
      secondaryText = {
        Text(
            text = strings.puzzlePlayingAs(puzzleInfo.playerColor.toString()),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
      },
  )
}
