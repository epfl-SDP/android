package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.tournaments.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/** An adapter that is of type [Contest] and contains the uid. */
data class ContestAdapter(
    val uid: String,
    override val name: String,
    override val creationDate: Duration,
    override val personStatus: ContestPersonStatus,
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
          createContest(
              "1", "EPFL Grand Prix", 1.days, ContestStatus.ONGOING, ContestPersonStatus.ADMIN),
          createContest(
              "2", "Pawn Party", 2.days, ContestStatus.DONE, ContestPersonStatus.PARTICIPANT),
          createContest(
              "3", "Never gonna chess", 3.hours, ContestStatus.ONGOING, ContestPersonStatus.VIEWER))
  override fun onNewContestClick() {}
}

private fun createContest(
    uid: String,
    name: String,
    duration: Duration,
    status: ContestStatus,
    personStatus: ContestPersonStatus
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
