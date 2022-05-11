package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
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
   * @param parameters The "TournamentParameters" that parametrize the user's "Tournament".
   */
  // TODO: Add the function corresponding to the documentation right above

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
          .map { it?.toTournament(user) }

  /**
   * Allows a user to advance the round number of a certain pool for a certain tournament, if the
   * user administrates the tournament.
   *
   * @param user The [AuthenticatedUser] that wants to join the "Tournament".
   * @param tournament The "Tournament" in which the pool is.
   * @param poolId The id of the pool in the "Tournament".
   */
  // TODO: Add the function corresponding to the documentation right above

  /**
   * Allows a user to advance the round number of direct eliminations for a certain tournament, if
   * the user administrates the tournament.
   *
   * @param user The [AuthenticatedUser] that wants to join the "Tournament".
   * @param tournament The "Tournament" to advance direct eliminations round.
   */
  // TODO: Add the function corresponding to the documentation right above

  /**
   * Allows a user to advance the direct elimination stage for a certain tournament, if the user
   * administrates the tournament.
   *
   * @param user The [AuthenticatedUser] that wants to join the "Tournament".
   * @param tournament The "Tournament" to advance the stage of direct eliminations.
   */
  // TODO: Add the function corresponding to the documentation right above
}
