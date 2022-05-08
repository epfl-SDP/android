package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.tournaments.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * An implementation of the [ContestScreenState] that performs a given profile's [Contest] requests.
 */
class TournamentScreenState() : ContestScreenState {

  // override var contests by mutableStateOf(emptyList<Contest>())
  // private set
  override val contests: List<Contest> =
      listOf(createContest("name", 1.days, ContestStatus.ONGOING, ContestPersonStatus.ADMIN))
  override fun onNewContestClick() {}
}

private fun createContest(
    name: String,
    duration: Duration,
    status: ContestStatus,
    personStatus: ContestPersonStatus
): Contest {
  return object : Contest {
    override val name = name
    override val creationDate = duration
    override val personStatus = personStatus
    override val status = status
  }
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
  ContestScreen(
      TournamentScreenState(), modifier, key = { "replace with Contest uid" }, contentPadding)
}
