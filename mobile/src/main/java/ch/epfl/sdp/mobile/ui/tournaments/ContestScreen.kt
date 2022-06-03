package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.Add
import ch.epfl.sdp.mobile.ui.Filter
import ch.epfl.sdp.mobile.ui.PawniesIcons

/**
 * This screen displays all the tournaments of the app.
 *
 * @param state the [ContestScreenState] to manage the contents of this composable.
 * @param modifier the [Modifier] for the composable.
 * @param key a function which uniquely identifies the list items.
 * @param contentPadding the [PaddingValues] for this screen.
 * @param C the type of the [ContestInfo].
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <C : ContestInfo> ContestScreen(
    state: ContestScreenState<C>,
    modifier: Modifier = Modifier,
    key: ((C) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(),
) {
  Scaffold(
      topBar = { TopAppBar(onFilterClick = state::onFilterClick) },
      modifier = modifier.padding(contentPadding),
      floatingActionButton = { NewContestButton(onClick = state::onNewContestClick) },
      content = { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxWidth(),
        ) {
          items(
              items = state.contests,
              key = key,
          ) { contest ->
            Contest(
                contestInfo = contest,
                onClick = { state.onContestClick(contest) },
                modifier = Modifier.animateItemPlacement(),
            )
          }
        }
      },
  )
}

/**
 * The top bar containing the title and tournaments filter.
 *
 * @param onFilterClick a callback called when the filter action is clicked.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun TopAppBar(
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier,
      color = MaterialTheme.colors.background,
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = LocalLocalizedStrings.current.tournamentsContestsTitle,
          color = MaterialTheme.colors.primary,
          style = MaterialTheme.typography.h4,
          modifier = Modifier.weight(1f),
      )
      IconButton(
          onClick = onFilterClick,
      ) { Icon(PawniesIcons.Filter, LocalLocalizedStrings.current.tournamentsFilter) }
    }
  }
}

/**
 * The button to create a new tournament.
 *
 * @param onClick callback function called when the button is clicked on.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun NewContestButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  ExtendedFloatingActionButton(
      text = { Text(LocalLocalizedStrings.current.newContest) },
      icon = { Icon(PawniesIcons.Add, null) },
      backgroundColor = MaterialTheme.colors.primary,
      onClick = onClick,
      modifier = modifier.heightIn(min = 56.dp),
  )
}
