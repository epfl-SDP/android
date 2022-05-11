package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo.Status
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreen
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreenState
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * An adapter that is of type [ContestInfo] and contains the uid.
 *
 * @param uid the unique identifier for this contest.
 * @param name the name of the contest.
 * @param badge the badge of the contest.
 * @param status the status of the contest.
 */
data class ContestInfoAdapter(
    val uid: String,
    override val name: String,
    override val badge: BadgeType?,
    override val status: Status,
) : ContestInfo

/**
 * An implementation of the [ContestScreenState] that performs a given profile's [ContestInfo]
 * requests.
 */
class TournamentScreenState : ContestScreenState<ContestInfoAdapter> {

  // TODO : Fill this in with some actual data.
  override val contests =
      listOf(
          createContest("1", "EPFL Grand Prix", Status.Ongoing(1.days), BadgeType.Admin),
          createContest("2", "Pawn Party", Status.Done, BadgeType.Participant),
          createContest("3", "Never gonna chess", Status.Ongoing(3.hours), BadgeType.Join),
      )

  override fun onNewContestClick() = Unit
  override fun onContestClick(contest: ContestInfoAdapter) = Unit
  override fun onFilterClick() = Unit
}

/**
 * TODO : Remove this.
 *
 * Creates a contest with some fake data.
 *
 * @param uid the id of the contest.
 * @param name the title of the contest.
 * @param status the [Status] for the contest.
 * @param personStatus the badge to display.
 */
private fun createContest(
    uid: String,
    name: String,
    status: Status,
    personStatus: BadgeType?
): ContestInfoAdapter {
  return ContestInfoAdapter(uid, name, personStatus, status)
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
