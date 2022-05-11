package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set

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
   * @param tournament The "Tournament" to join.
   */
  // TODO: Add the function corresponding to the documentation right above

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
   */
  suspend fun createTournament(
      user: AuthenticatedUser,
      name: String,
      maxPlayers: Int,
      bestOf: Int,
      poolSize: Int,
      eliminationRounds: Int,
  ): TournamentReference {
    val document = store.collection("tournaments").document()
    document.set(
        TournamentDocument(
            adminId = user.uid,
            name = name,
            maxPlayers = maxPlayers,
            bestOf = bestOf,
            poolSize = poolSize,
            eliminationRounds = eliminationRounds,
        ))

    return TournamentReference(uid = document.id)
  }

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
