package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.tournaments.*
import ch.epfl.sdp.mobile.ui.tournaments.Status.*
import ch.epfl.sdp.mobile.ui.tournaments.Status.ContestStatus.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/** An adapter that is of type [Contest] and contains the uid. */
data class ContestAdapter(
    val uid: String,
    override val name: String,
    override val creationTime: Duration,
    override val personStatus: BadgeType,
    override val status: ContestStatus
) : Contest {}

/**
 * An implementation of the [ContestScreenState] that performs a given profile's [Contest] requests.
 */
class TournamentScreenState() : ContestScreenState<ContestAdapter> {

  // override var contests by mutableStateOf(emptyList<Contest>())
  // private set
  override val contests =
      listOf(
          createContest("1", "EPFL Grand Prix", 1.days, ONGOING, BadgeType.Admin),
          createContest("2", "Pawn Party", 2.days, DONE, BadgeType.Participant),
          createContest("3", "Never gonna chess", 3.hours, ONGOING, BadgeType.Join))
  override fun onNewContestClick() {}
  override fun onContestClick(C: Contest) {}
}

private fun createContest(
    uid: String,
    name: String,
    duration: Duration,
    status: ContestStatus,
    personStatus: BadgeType
): ContestAdapter {
  return ContestAdapter(uid, name, duration, personStatus, status)
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
  ContestScreen(TournamentScreenState(), modifier, key = { it.uid }, contentPadding)
}
