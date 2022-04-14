package ch.epfl.sdp.mobile.ui.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.social.*

/**
 * A Slot Component for Profile or Setting Screen
 * @param header part of slot construct that comes into the header
 * @param profileBar part of slot construct that represents the tab bar.
 * @param matches the part that is responsible for the list of all matches
 * @param lazyColumnState to keep state of LazyColumn onMatchClick a callback called when a
 * [ChessMatch] is clicked
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserScreen(
    header: @Composable () -> Unit,
    profileTabBar: @Composable () -> Unit,
    matches: List<ChessMatch>,
    onMatchClick: (ChessMatch) -> Unit,
    lazyColumnState: LazyListState,
    modifier: Modifier = Modifier,
) {
  LazyColumn(
      state = lazyColumnState,
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier,
  ) {
    item { header() }
    stickyHeader { profileTabBar() }
    items(matches) { match -> Match(match, { onMatchClick(match) }) }
  }
}
