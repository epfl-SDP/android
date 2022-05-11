package ch.epfl.sdp.mobile.state.tournaments

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Tournament
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.ui.tournaments.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** An object representing a [Tournament] which is still loading. */
object EmptyTournament : Tournament {
  override val reference = TournamentReference("")
  override val name = ""
  override val isAdmin = false
  override val isParticipant = false
}

/**
 * An implementation of [TournamentDetailsState] which uses a [TournamentReference] to fetch a
 * [Tournament], and delegate some responsibility to it.
 *
 * @param actions the [TournamentDetailsActions] to be called.
 * @param user the [AuthenticatedUser] which is currently connected.
 * @param facade the [TournamentFacade] which provides access to tournament information.
 * @param reference the [TournamentReference] of the tournament we're loading.
 * @param scope the [CoroutineScope] on which the tournament is loaded.
 */
class ActualTournamentDetailsState(
    actions: State<TournamentDetailsActions>,
    private val user: AuthenticatedUser,
    private val facade: TournamentFacade,
    private val reference: TournamentReference,
    private val scope: CoroutineScope,
) : TournamentDetailsState<PoolMember, TournamentMatch> {

  /** The current [TournamentDetailsActions]. */
  private val actions by actions

  /** The current [Tournament]. */
  private var tournament by mutableStateOf<Tournament>(EmptyTournament)

  init {
    scope.launch {
      facade
          .tournament(reference, user)
          .map { it ?: EmptyTournament }
          .onEach { tournament = it }
          .collect()
    }
  }

  override val badge: BadgeType?
    get() =
        when {
          // TODO : Detect tournaments which are not started yet.
          tournament.isAdmin -> BadgeType.Admin
          tournament.isParticipant -> BadgeType.Participant
          else -> BadgeType.Join
        }

  override val title: String
    get() = tournament.name

  override val pools: List<PoolInfo<PoolMember>> = emptyList()

  override val finals: List<TournamentsFinalsRound<TournamentMatch>> = emptyList()

  override fun onBadgeClick() {
    scope.launch { facade.join(user, reference) }
  }

  override fun onWatchMatchClick(match: TournamentMatch) = Unit

  override fun onCloseClick() = actions.onBackClick()
}
