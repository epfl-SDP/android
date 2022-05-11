package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Tournament
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo.Status
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreen
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A class that turns a [Tournament] into a [ContestInfo].
 *
 * @param tournament the [Tournament] to transform into a [ContestInfo].
 * @param currentUser the current [AuthenticatedUser].
 */
data class TournamentAdapter(val tournament: Tournament, val currentUser: AuthenticatedUser) :
    ContestInfo {
  val uid = tournament.reference.uid
  override val name: String = tournament.name
  override val badge: BadgeType? =
      if (tournament.isAdmin) {
        BadgeType.Admin
      } else if (tournament.isParticipant) {
        BadgeType.Participant
      } else {
        BadgeType.Join
      }
  // TODO: Change to tournament.status when added.
  override val status: Status = Status.Done
}

/**
 * An implementation of the [ContestScreenState] that performs a given profile's [ContestInfo]
 * requests.
 *
 * @param currentUser the current [AuthenticatedUser] of the application.
 * @param tournamentFacade the [TournamentFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class TournamentScreenState(
    private val currentUser: AuthenticatedUser,
    private val tournamentFacade: TournamentFacade,
    private val scope: CoroutineScope,
) : ContestScreenState<TournamentAdapter> {

  override var contests by mutableStateOf(emptyList<TournamentAdapter>())
    private set

  init {
    scope.launch {
      tournamentFacade.getTournaments(currentUser).collect { list ->
        contests = list.map { TournamentAdapter(it, currentUser) }
      }
    }
  }

  override fun onNewContestClick() = Unit
  override fun onContestClick(contest: TournamentAdapter) = Unit
  override fun onFilterClick() = Unit
}

/**
 * A stateful composable to view the list of tournaments completed and ongoing.
 *
 * @param currentUser the current [AuthenticatedUser] of the application.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun StatefulTournamentScreen(
    currentUser: AuthenticatedUser,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val tournamentFacade = LocalTournamentFacade.current
  val scope = rememberCoroutineScope()

  ContestScreen(
      TournamentScreenState(currentUser, tournamentFacade, scope),
      modifier,
      key = { it.uid },
      contentPadding)
}
