package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.WhiteKing
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleListInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch

/**
 * A Slot Component for Profile or Setting Screen.
 *
 * @param C the type of the [ChessMatch].
 * @param P the type of the [PuzzleInfo].
 * @param header part of slot construct that comes into the header.
 * @param profileTabBar part of slot construct that represents the tab bar.
 * @param tabBarState the state of the tab bar.
 * @param matches the part that is responsible for the list of all matches.
 * @param onMatchClick callback function executed when a match is clicked on.
 * @param puzzles the list of puzzles to display.
 * @param onPuzzleClick callback function executed when a puzzle is clicked on.
 * @param lazyColumnState to keep state of LazyColumn onMatchClick a callback called when a
 * [ChessMatch] is clicked.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this screen.
 * @param matchKey the key for each match item.
 * @param puzzleKey the key for each puzzle item.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <C : ChessMatch, P : PuzzleInfo> UserScreen(
    header: @Composable () -> Unit,
    profileTabBar: @Composable () -> Unit,
    tabBarState: ProfileTabBarState,
    matches: List<C>,
    onMatchClick: (C) -> Unit,
    puzzles: List<P>,
    onPuzzleClick: (P) -> Unit,
    lazyColumnState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    matchKey: ((C) -> Any)? = null,
    puzzleKey: ((P) -> Any)? = null,
) {

  LazyColumn(
      state = lazyColumnState,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
      contentPadding = contentPadding,
  ) {
    item { header() }
    stickyHeader { profileTabBar() }
    when (tabBarState.currentTab) {
      ProfileTabBarState.Tab.PastGames ->
          items(
              items = matches,
              key = matchKey,
          ) { match ->
            Match(
                onClick = { onMatchClick(match) },
                match = match,
                icon = ChessIcons.WhiteKing,
            )
          }
      ProfileTabBarState.Tab.Puzzles ->
          items(
              items = puzzles,
              key = puzzleKey,
          ) { puzzle ->
            PuzzleListInfo(
                puzzleInfo = puzzle,
                onClick = { onPuzzleClick(puzzle) },
            )
          }
    }
  }
}
