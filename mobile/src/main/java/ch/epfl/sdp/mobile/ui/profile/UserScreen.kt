package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.WhiteKing
import ch.epfl.sdp.mobile.ui.social.*

/**
 * A Slot Component for Profile or Setting Screen.
 *
 * @param C the type of the [ChessMatch].
 * @param header part of slot construct that comes into the header.
 * @param profileTabBar part of slot construct that represents the tab bar.
 * @param matches the part that is responsible for the list of all matches.
 * @param onMatchClick callback function executed when a match is clicked on.
 * @param lazyColumnState to keep state of LazyColumn onMatchClick a callback called when a
 * [ChessMatch] is clicked.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <C : ChessMatch> UserScreen(
    header: @Composable () -> Unit,
    profileTabBar: @Composable () -> Unit,
    matches: List<C>,
    onMatchClick: (C) -> Unit,
    lazyColumnState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
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
    items(matches) { match -> Match(match, ChessIcons.WhiteKing, onMatchClick) }
  }
}
