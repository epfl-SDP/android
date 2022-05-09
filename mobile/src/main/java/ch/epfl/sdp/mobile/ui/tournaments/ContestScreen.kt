package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ch.epfl.sdp.mobile.ui.plus

/**
 * This screen displays all the tournaments of the app.
 *
 * @param state the [ContestScreenState] to manage the contents of this composable.
 * @param modifier the [Modifier] for the composable.
 * @param key a function which uniquely identifies the list items.
 * @param contentPadding the [PaddingValues] for this screen.
 * @param C the type of the [Contest].
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <C : Contest> ContestScreen(
    state: ContestScreenState<C>,
    modifier: Modifier = Modifier,
    key: ((C) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues()
) {

  Scaffold(
      topBar = { TopAppBar() },
      modifier = modifier.padding(contentPadding),
      floatingActionButton = { NewContestButton(onClick = state::onNewContestClick) },
      content = { innerPadding ->
        val totalPadding = contentPadding + innerPadding
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = totalPadding,
            modifier = Modifier.fillMaxWidth()) {
          items(state.contests, key) { contest -> ContestItem(contest = contest, onClick = {}) }
        }
      })
}

/** The top bar containing the title and tournaments filter. */
@Composable
fun TopAppBar() {
  val strings = LocalLocalizedStrings.current
  TopAppBar(
      title = {
        Text(
            text = strings.tournamentsContestsTitle,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h4)
      },
      elevation = 0.dp,
      actions = { Icon(PawniesIcons.Filter, null) },
      backgroundColor = MaterialTheme.colors.background,
      contentColor = MaterialTheme.colors.primary,
      modifier = Modifier.padding(top = 12.dp, end = 16.dp))
}

/**
 * The button to create a new tournament.
 *
 * @param onClick callback function called when the button is clicked on.
 * @param modifier the [Modifier] for this composable.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewContestButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
  val strings = LocalLocalizedStrings.current

  FloatingActionButton(
      onClick = onClick,
      shape = RoundedCornerShape(28.dp),
      backgroundColor = MaterialTheme.colors.onSurface,
      contentColor = MaterialTheme.colors.onPrimary,
      modifier = modifier) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
      Icon(PawniesIcons.Add, null)
      Spacer(Modifier.width(8.dp))
      Text(strings.newContest)
    }
  }
}
