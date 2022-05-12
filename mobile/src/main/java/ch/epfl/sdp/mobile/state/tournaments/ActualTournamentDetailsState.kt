package ch.epfl.sdp.mobile.state.tournaments

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.Pool
import ch.epfl.sdp.mobile.application.tournaments.Tournament
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.NotStarted
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.ui.tournaments.*
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.*
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.StartTournamentBanner.EnoughPlayers
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.StartTournamentBanner.NotEnoughPlayers
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
  override val status = Status.Unknown
  override suspend fun start(): Boolean = false
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

  private var poolsState by mutableStateOf<List<Pool>>(emptyList())

  init {
    scope.launch { facade.pools(reference, user).onEach { poolsState = it }.collect() }
  }

  override val badge: BadgeType?
    get() =
        when {
          // Let admins join their tournament.
          !tournament.isParticipant && tournament.status is NotStarted -> BadgeType.Join
          tournament.isAdmin -> BadgeType.Admin
          tournament.isParticipant -> BadgeType.Participant
          else -> null
        }

  override val title: String
    get() = tournament.name

  override val pools: List<PoolInfo<PoolMember>>
    get() =
        poolsState.map { pool ->
          object : PoolInfo<PoolMember> {
            override val name: String = pool.name
            override val status: PoolInfo.Status =
                PoolInfo.Status.Ongoing(
                    currentRound = pool.totalRounds - pool.remainingRounds + 1,
                    totalRounds = pool.totalRounds,
                )
            override val startNextRoundEnabled: Boolean = pool.isStartNextRoundEnabled
            override fun onStartNextRound() = Unit
            override val members: List<PoolMember>
              get() =
                  pool.players.map { player ->
                    object : PoolMember {
                      override val name: String = player.name
                      override val total: PoolScore? = 0
                    }
                  }

            override fun PoolMember.scoreAgainst(other: PoolMember): PoolScore? = 0
          }
        }

  override val finals: List<TournamentsFinalsRound<TournamentMatch>> = emptyList()

  override val startTournamentBanner: StartTournamentBanner?
    get() {
      val status = tournament.status
      return if (tournament.isAdmin && status is NotStarted) {
        if (status.enoughParticipants) EnoughPlayers else NotEnoughPlayers
      } else null
    }

  override fun onStartTournament() {
    scope.launch { tournament.start() }
  }

  override fun onBadgeClick() {
    scope.launch { facade.join(user, reference) }
  }

  override fun onWatchMatchClick(match: TournamentMatch) = Unit

  override fun onCloseClick() = actions.onBackClick()
}
