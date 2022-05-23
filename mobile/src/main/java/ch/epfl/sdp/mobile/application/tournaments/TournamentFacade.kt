package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.PoolDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import kotlin.math.log2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * An interface which represents all the endpoints and available features for tournaments.
 *
 * @param auth the [Auth] instance which will be used to handle authentication.
 * @param store the [Store] which is used to manage documents.
 */
class TournamentFacade(private val auth: Auth, private val store: Store) {

  /**
   * Returns all of the registered tournaments of the application.
   *
   * @param user the current [AuthenticatedUser].
   */
  // TODO : Add .orderBy("creationDate", Descending) once creationDate defined.
  fun tournaments(user: AuthenticatedUser): Flow<List<Tournament>> {
    return store.collection(TournamentDocument.Collection).asFlow<TournamentDocument>().map {
      it.mapNotNull { doc -> doc?.toTournament(user, store) }
    }
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
              creationTimeEpochMillis = System.currentTimeMillis(),
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
          .map { it?.toTournament(user, store) }

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
          .collection("games")
          .whereEquals("tournamentId", reference.uid)
          .whereNotEquals("poolId", null)
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
          .collection("games")
          .whereEquals("tournamentId", reference.uid)
          .whereEquals("poolId", null)
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
