package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreen
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreenState

/**
 * An implementation of the [ContestScreenState] that performs a given profile's [Contest] requests.
 */
class TournamentScreenState() : ContestScreenState {

  override fun onNewContestClick() {}
}

/**
 * A stateful composable to view the list of tournaments completed and ongoing.
 *
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun StatefulTournamentScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  ContestScreen(TournamentScreenState(), modifier, contentPadding)
}
