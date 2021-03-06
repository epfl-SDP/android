package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Tournament
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.application.tournaments.isDone
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType.Admin
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType.Participant
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo.Status
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreen
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A class that turns a [Tournament] into a [ContestInfo].
 *
 * @property tournament the [Tournament] to transform into a [ContestInfo].
 * @property currentUser the current [AuthenticatedUser].
 */
data class TournamentAdapter(
    val tournament: Tournament,
    val currentUser: AuthenticatedUser,
) : ContestInfo {

  /** The unique identifier for the underlying [Tournament]. */
  val uid = tournament.reference.uid

  override val name: String = tournament.name
  override val badge: BadgeType? =
      when {
        tournament.isAdmin -> Admin
        tournament.isParticipant -> Participant
        else -> null
      }

  override val status: Status =
      if (!tournament.isDone()) {
        Status.Ongoing(tournament.durationCreated)
      } else {
        Status.Done
      }
}

/**
 * An implementation of the [ContestScreenState] that performs a given profile's [ContestInfo]
 * requests.
 *
 * @param actions the [TournamentActions] which are available on the screen.
 * @property currentUser the current [AuthenticatedUser] of the application.
 * @property tournamentFacade the [TournamentFacade] used to perform some requests.
 * @property scope the [CoroutineScope] on which requests are performed.
 */
class TournamentScreenState(
    actions: State<TournamentActions>,
    private val currentUser: AuthenticatedUser,
    private val tournamentFacade: TournamentFacade,
    private val scope: CoroutineScope,
) : ContestScreenState<TournamentAdapter> {
  private val actions by actions
  override var contests by mutableStateOf(emptyList<TournamentAdapter>())
    private set

  init {
    scope.launch {
      tournamentFacade.tournaments(currentUser).collect { list ->
        contests = list.map { TournamentAdapter(it, currentUser) }
      }
    }
  }

  override fun onNewContestClick() = actions.onNewContestClick()
  override fun onContestClick(contest: TournamentAdapter) =
      actions.onTournamentClick(contest.tournament.reference)
  override fun onFilterClick() = actions.onFilterClick()
}

/**
 * A stateful composable to view the list of tournaments completed and ongoing.
 *
 * @param currentUser the current [AuthenticatedUser] of the application.
 * @param onTournamentClick callback called when a tournament item is clicked on.
 * @param onNewContestClickAction callback called when the new contest button is clicked on.
 * @param onFilterClick a callback which is called when the user wants to show the filters dialog.
 * @param modifier the [Modifier] for this composable.
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun StatefulTournamentScreen(
    currentUser: AuthenticatedUser,
    onTournamentClick: (TournamentReference) -> Unit,
    onNewContestClickAction: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val actions =
      rememberUpdatedState(
          TournamentActions(
              onTournamentClick = onTournamentClick,
              onNewContestClick = onNewContestClickAction,
              onFilterClick = onFilterClick,
          ),
      )
  val tournamentFacade = LocalTournamentFacade.current
  val scope = rememberCoroutineScope()
  val state =
      remember(actions, currentUser, tournamentFacade, scope) {
        TournamentScreenState(actions, currentUser, tournamentFacade, scope)
      }

  ContestScreen(state, modifier, key = { it.uid }, contentPadding)
}

/**
 * A class representing the different actions available on the tournament screen.
 *
 * @property onTournamentClick callback called when a tournament item is clicked on.
 * @property onNewContestClick callback called when the new contest button is clicked on.
 * @property onFilterClick callback called when the filter action is pressed.
 */
data class TournamentActions(
    val onTournamentClick: (TournamentReference) -> Unit,
    val onNewContestClick: () -> Unit,
    val onFilterClick: () -> Unit,
)
