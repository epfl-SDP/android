package ch.epfl.sdp.mobile.state.tournaments

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.tournaments.*
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.NotStarted
import ch.epfl.sdp.mobile.application.tournaments.Tournament.Status.Pools
import ch.epfl.sdp.mobile.ui.tournaments.*
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.*
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.PoolBanner.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** An object representing a [Tournament] which is still loading. */
object EmptyTournament : Tournament {
  override val reference = TournamentReference("")
  override val name = ""
  override val durationCreated = 0.milliseconds
  override val isAdmin = false
  override val isParticipant = false
  override val status = Status.Unknown
  override suspend fun start() = Unit
  override suspend fun startDirectElimination() = Unit
  override suspend fun startNextRound() = Unit
}

/** An object representing a [PoolResults] which is still loading. */
object EmptyPoolResults : PoolResults {
  override val players: List<String> = emptyList()
  override fun against(player: String, opponent: String): Int = 0
  override fun score(playerId: String): Int = 0
  override fun played(playerId: String): Int = 0
}

/**
 * A class representing a [PoolMember] uniquely identified by an identifier.
 *
 * @param id the unique identifier of the pool member.
 * @param name the name of the pool member.
 * @param results a function to retrieve the pool results.
 */
class PlayerIdPoolMember(
    val id: String,
    override val name: String,
    private val results: () -> PoolResults,
) : PoolMember {
  override val total: PoolScore
    get() = results().score(id)
}

/**
 * A class representing some [PoolInfo] which uses some [PlayerIdPoolMember]s.
 *
 * @param pool the underlying [Pool].
 * @param scope the [CoroutineScope] used to move to the next round.
 * @param results a function to retrieve the pool results.
 */
class PlayerIdPoolInfo(
    private val pool: Pool,
    private val scope: CoroutineScope,
    private val results: () -> PoolResults,
) : PoolInfo<PlayerIdPoolMember> {

  override val name: String = pool.name

  override val status: PoolInfo.Status =
      PoolInfo.Status.Ongoing(
          currentRound = pool.totalRounds - pool.remainingRounds,
          totalRounds = pool.totalRounds,
      )

  override val startNextRoundEnabled: Boolean = pool.isStartNextRoundEnabled

  override fun onStartNextRound() {
    scope.launch { pool.startNextRound() }
  }

  override val members: List<PlayerIdPoolMember>
    get() =
        pool.players.map { player ->
          PlayerIdPoolMember(
              id = player.uid,
              name = player.name,
              results = results,
          )
        }

  override fun PlayerIdPoolMember.scoreAgainst(other: PlayerIdPoolMember): PoolScore =
      results().against(this.id, other.id)
}

/**
 * An adapter for [TournamentMatch] which wraps an [EliminationMatch].
 *
 * @param match the [EliminationMatch] that is wrapped.
 */
class EliminationMatchAdapter(match: EliminationMatch) : TournamentMatch {
  val id = match.id
  val depth = match.depth
  override val firstPlayerName = match.whiteName
  override val secondPlayerName = match.blackName
  override val result =
      when (match.status) {
        EliminationMatch.Status.WhiteWon -> TournamentMatch.Result.FirstWon
        EliminationMatch.Status.BlackWon -> TournamentMatch.Result.SecondWon
        EliminationMatch.Status.Drawn -> TournamentMatch.Result.Draw
        EliminationMatch.Status.None -> TournamentMatch.Result.Ongoing
      }
}

/**
 * An implementation of [TournamentsFinalsRound] which uses a [Status.Round] and a list of all the
 * matches.
 *
 * @param tournament the [Tournament] that is being used.
 * @param scope the [CoroutineScope] used to launch the next rounds.
 * @param round the [Status.Round].
 * @param allMatches the [EliminationMatch]es to display.
 */
class EliminationMatchAdapterTournamentsFinalsRound(
    private val tournament: Tournament,
    private val scope: CoroutineScope,
    round: Status.Round,
    allMatches: List<EliminationMatchAdapter>,
) : TournamentsFinalsRound<EliminationMatchAdapter> {
  override val name = round.name
  override val matches = allMatches.filter { it.depth == round.depth }
  override val banner: TournamentsFinalsRound.Banner? =
      if (round.moveToNextRoundEnabled) TournamentsFinalsRound.Banner.NextRound else null
  override fun onBannerClick() {
    scope.launch { tournament.startNextRound() }
  }
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
) : TournamentDetailsState<PlayerIdPoolMember, EliminationMatchAdapter> {

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

  private var poolResultsState by mutableStateOf<PoolResults>(EmptyPoolResults)

  init {
    scope.launch { facade.poolResults(reference).onEach { poolResultsState = it }.collect() }
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

  override val pools: List<PoolInfo<PlayerIdPoolMember>>
    get() =
        poolsState.map { pool ->
          PlayerIdPoolInfo(
              pool = pool,
              scope = scope,
              results = { poolResultsState },
          )
        }

  private var eliminationMatchesState by mutableStateOf(emptyList<EliminationMatchAdapter>())

  init {
    scope.launch {
      facade
          .eliminationMatches(reference)
          .onEach { list -> eliminationMatchesState = list.map { EliminationMatchAdapter(it) } }
          .collect()
    }
  }

  override val finals: List<TournamentsFinalsRound<EliminationMatchAdapter>>
    get() =
        when (val status = tournament.status) {
          is Status.DirectElimination ->
              status.rounds.map {
                EliminationMatchAdapterTournamentsFinalsRound(
                    tournament = tournament,
                    scope = scope,
                    round = it,
                    allMatches = eliminationMatchesState,
                )
              }
          else -> emptyList()
        }

  override val poolBanner: PoolBanner?
    get() {
      val status = tournament.status
      return when {
        tournament.isAdmin && status is NotStarted -> {
          if (status.enoughParticipants) EnoughPlayers else NotEnoughPlayers
        }
        tournament.isAdmin && status == Pools -> {
          StartDirectElimination
        }
        else -> null
      }
    }

  override fun onPoolBannerClick() {
    when (poolBanner) {
      StartDirectElimination -> scope.launch { tournament.startDirectElimination() }
      EnoughPlayers, NotEnoughPlayers -> scope.launch { tournament.start() }
      null -> Unit
    }
  }

  override fun onBadgeClick() {
    scope.launch { facade.join(user, reference) }
  }

  override fun onWatchMatchClick(match: EliminationMatchAdapter) = actions.onMatchClick(match.id)

  override fun onCloseClick() = actions.onBackClick()
}
