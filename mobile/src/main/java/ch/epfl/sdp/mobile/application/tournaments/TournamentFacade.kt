package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.*
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import ch.epfl.sdp.mobile.infrastructure.time.TimeProvider
import kotlin.math.log2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * An interface which represents all the endpoints and available features for tournaments.
 *
 * @param auth the [Auth] instance which will be used to handle authentication.
 * @param dataStoreFactory the [DataStoreFactory] which will be used to access the preferences.
 * @param store the [Store] which is used to manage documents.
 * @param timeProvider the [TimeProvider] used to calculate the duration of creation of the
 * tournament.
 */
class TournamentFacade(
    private val auth: Auth,
    private val dataStoreFactory: DataStoreFactory,
    private val store: Store,
    private val timeProvider: TimeProvider,
) {

  /** An object will all the DataStore keys specific to the [TournamentFacade]. */
  private object DataStoreKeys {

    /** @see TournamentFacade.keyFilterShowDone */
    const val ShowDone = "show_done"

    /** @see TournamentFacade.keyFilterShowParticipating */
    const val ShowParticipating = "show_participating"

    /** @see TournamentFacade.keyFilterShowAdministrating */
    const val ShowAdministrating = "show_administrating"
  }

  /** The [DataStore] instance in which the preferences are stored. */
  private val dataStore: DataStore<Preferences>

  /** The key which indicates if only done tournaments should be displayed. */
  private val keyFilterShowDone: Key<Boolean>

  /** The key which indicates if only participating tournaments should be displayed. */
  private val keyFilterShowParticipating: Key<Boolean>

  /** The key which indicates if only administrating tournaments should be displayed. */
  private val keyFilterShowAdministrating: Key<Boolean>

  init {
    val (prefs, factory) = dataStoreFactory.createPreferencesDataStore()
    dataStore = prefs
    keyFilterShowDone = factory.boolean(DataStoreKeys.ShowDone)
    keyFilterShowParticipating = factory.boolean(DataStoreKeys.ShowParticipating)
    keyFilterShowAdministrating = factory.boolean(DataStoreKeys.ShowAdministrating)
  }

  /**
   * A class representing the filters applied to the tournaments.
   *
   * @property showDone true if only the done tournaments are displayed.
   * @property showParticipating true if only the participating tournaments are displayed.
   * @property showAdministrating true if only the administrating tournaments are displayed.
   * @param facade the [TournamentFacade] in which the filters are managed.
   */
  class TournamentFilters(
      val showDone: Boolean,
      val showParticipating: Boolean,
      val showAdministrating: Boolean,
      private val facade: TournamentFacade
  ) {

    /**
     * Updates the current tournament filters.
     *
     * @param block the [UpdateScope] in which some updates may be performed.
     */
    suspend fun update(block: UpdateScope.() -> Unit) =
        facade.dataStore.edit { UpdateScope(facade, it).apply(block) }

    /**
     * A scope which allows edition to the [TournamentFilters].
     *
     * @param facade the [TournamentFilters] in which the filters are managed.
     * @param preferences the [MutablePreferences] which are currently edited.
     */
    class UpdateScope(
        private val facade: TournamentFacade,
        private val preferences: MutablePreferences,
    ) {

      /** Sets the "show done" filter to [enabled]. */
      fun onlyShowDone(
          enabled: Boolean,
      ) = preferences.set(facade.keyFilterShowDone, enabled)

      /** Sets the "show participating" filter to [enabled]. */
      fun onlyShowParticipating(
          enabled: Boolean,
      ) = preferences.set(facade.keyFilterShowParticipating, enabled)

      /** Sets the "show administrating" filter to [enabled]. */
      fun onlyShowAdministrating(
          enabled: Boolean,
      ) = preferences.set(facade.keyFilterShowAdministrating, enabled)
    }
  }

  /** Returns a [Flow] of the currently applied [TournamentFilters]. */
  fun filters(): Flow<TournamentFilters> =
      dataStore.data.map {
        val onlyShowDone = it[keyFilterShowDone] ?: false
        val onlyShowParticipating = it[keyFilterShowParticipating] ?: false
        val onlyShowAdministering = it[keyFilterShowAdministrating] ?: false
        TournamentFilters(
            showDone = onlyShowDone,
            showParticipating = onlyShowParticipating,
            showAdministrating = onlyShowAdministering,
            facade = this,
        )
      }

  /**
   * Returns a [Flow] of predicates on a tournament, which return true if the tournament should be
   * displayed to the user.
   *
   * @return the [Flow] of predicates.
   */
  private fun tournamentPredicate(): Flow<(Tournament) -> Boolean> =
      filters().map { filters ->
        { t: Tournament ->
          // TODO : Use showDone when tournament statuses work.
          (!filters.showParticipating || t.isParticipant) &&
              (!filters.showAdministrating || t.isAdmin)
        }
      }

  /**
   * Returns all of the registered tournaments of the application in descending order of their
   * creation date.
   *
   * @param user the current [AuthenticatedUser].
   */
  fun tournaments(user: AuthenticatedUser): Flow<List<Tournament>> {
    val tournaments =
        store
            .collection(TournamentDocument.Collection)
            .orderBy(TournamentDocument.CreationTimeEpochMillis, Query.Direction.Descending)
            .asFlow<TournamentDocument>()
            .map { it.mapNotNull { doc -> doc?.toTournament(user, store, timeProvider) } }
    val predicates = tournamentPredicate()
    return combine(tournaments, predicates) { t, p -> t.filter(p) }
  }

  /**
   * Allows a user to join an ongoing tournament.
   *
   * @param user The [AuthenticatedUser] that wants to join the "Tournament".
   * @param reference The "Tournament" reference to join.
   */
  suspend fun join(user: AuthenticatedUser, reference: TournamentReference) {
    store.collection(TournamentDocument.Collection).document(reference.uid).update {
      arrayUnion(TournamentDocument.Participants, user.uid)
    }
  }

  /**
   * Allows a user to create a tournament. The user in question administrates the tournament.
   *
   * @param user The [AuthenticatedUser] that wants to join the "Tournament".
   * @param name The name of the tournament.
   * @param maxPlayers The maximal number of players allowed to join the tournament.
   * @param bestOf The number of best-of rounds.
   * @param poolSize The target size of each pool.
   * @param eliminationRounds The number of direct elimination rounds. Directly influences number of
   * player selected from the pool phase.
   *
   * @return The corresponding [TournamentReference] of the parameters are valid, null otherwise.
   */
  suspend fun createTournament(
      user: AuthenticatedUser,
      name: String,
      maxPlayers: Int,
      bestOf: Int,
      poolSize: Int,
      eliminationRounds: Int,
  ): TournamentReference? {
    if (validParameters(
        name = name,
        bestOf = bestOf,
        poolSize = poolSize,
        eliminationRounds = eliminationRounds,
        maximumPlayerCount = maxPlayers,
    )) {
      val document = store.collection(TournamentDocument.Collection).document()
      document.set(
          TournamentDocument(
              adminId = user.uid,
              name = name,
              maxPlayers = maxPlayers,
              creationTimeEpochMillis = timeProvider.now(),
              bestOf = bestOf,
              poolSize = poolSize,
              eliminationRounds = eliminationRounds,
          ))

      return TournamentReference(uid = document.id)
    } else {
      return null
    }
  }

  /**
   * Returns the [Flow] of [Tournament] for a given [TournamentReference].
   *
   * @param reference the uniquely identifying [TournamentReference] for the tournament we're
   * fetching.
   * @param user the [AuthenticatedUser] that is fetching the tournament.
   * @return the [Flow] of [Tournament].
   */
  fun tournament(
      reference: TournamentReference,
      user: AuthenticatedUser,
  ): Flow<Tournament?> =
      store
          .collection(TournamentDocument.Collection)
          .document(reference.uid)
          .asFlow<TournamentDocument>()
          .map { it?.toTournament(user, store, timeProvider) }

  /**
   * Returns the [Flow] of the [List] of [Pool]s for a given [TournamentReference].
   *
   * @param reference the uniquely identifying [TournamentReference] for the tournament we're
   * fetching.
   * @param user the [AuthenticatedUser] that is fetching the pools.
   * @return the [Flow] of [Pool]s.
   */
  fun pools(
      reference: TournamentReference,
      user: AuthenticatedUser,
  ): Flow<List<Pool>> =
      store
          .collection(PoolDocument.Collection)
          .whereEquals(PoolDocument.TournamentId, reference.uid)
          .orderBy(PoolDocument.Name)
          .asFlow<PoolDocument>()
          .map { list -> list.mapNotNull { it?.toPool(user, store) } }

  /**
   * Returns the [Flow] of the [PoolResults] for a given [TournamentReference].
   *
   * @param reference the uniquely identifying [TournamentReference] for the tournament we're
   * fetching.
   * @return the [Flow] of [PoolResults].
   */
  fun poolResults(reference: TournamentReference): Flow<PoolResults> =
      store
          .collection(ChessDocument.Collection)
          .whereEquals(ChessDocument.TournamentId, reference.uid)
          .whereNotEquals(ChessDocument.PoolId, null)
          .asFlow<ChessDocument>()
          .map { it.filterNotNull().toPoolResults() }

  /**
   * Returns the [Flow] of all the [EliminationMatch] in this [TournamentReference].
   *
   * @param reference the [TournamentReference] for this tournament.
   * @return the [Flow] of [EliminationMatch].
   */
  fun eliminationMatches(reference: TournamentReference): Flow<List<EliminationMatch>> =
      store
          .collection(ChessDocument.Collection)
          .whereEquals(ChessDocument.TournamentId, reference.uid)
          .whereEquals(ChessDocument.PoolId, null)
          .asFlow<ChessDocument>()
          .map { list -> list.mapNotNull { it?.toEliminationMatch() } }

  /**
   * Validates the parameters of a tournament
   *
   * @param name The name of the tournament parameter.
   * @param bestOf The number of "best-of" rounds parameter.
   * @param maximumPlayerCount The maximum player count parameter.
   * @param poolSize The target pool size parameter.
   * @param eliminationRounds The number of elimination rounds parameter.
   *
   * @return True if the parameters are valid, otherwise false.
   */
  fun validParameters(
      name: String,
      bestOf: Int?,
      maximumPlayerCount: Int?,
      poolSize: Int?,
      eliminationRounds: Int?,
  ): Boolean {
    val players = maximumPlayerCount ?: 0
    val depth = log2((players / 2).toDouble()).toInt() + 1

    return if (bestOf != null &&
        poolSize != null &&
        eliminationRounds != null &&
        maximumPlayerCount != null) {
      name.isNotBlank() && poolSize <= players && eliminationRounds <= depth
    } else {
      false
    }
  }
}
