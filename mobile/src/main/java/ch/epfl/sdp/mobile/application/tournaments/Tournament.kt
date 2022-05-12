package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store

/** An interface which represents information about a fetched tournament. */
interface Tournament {

  /** The [TournamentReference], which can be used to uniquely identify this tournament. */
  val reference: TournamentReference

  /** The unique name for this [Tournament]. */
  val name: String

  /** True iff the user who fetched the [Tournament] participates in it. */
  val isAdmin: Boolean

  /** True iff the user who fetched the [Tournament] participates in it. */
  val isParticipant: Boolean

  /** The [Status] of the [Tournament]. */
  val status: Status

  /** A sealed interface representing the current status of this [Tournament]. */
  sealed interface Status {

    /**
     * Indicates that the tournament has not been started yet.
     *
     * @param enoughParticipants true iff there would be enough players to play all the matches.
     */
    data class NotStarted(val enoughParticipants: Boolean) : Status

    /** Indicates that we are currently in the pools phase. */
    object Pools : Status

    /** Indicates that the status is still loading and is not known yet. */
    object Unknown : Status
  }

  /**
   * Starts this tournament. If the user isn't the admin or the tournament was already started, this
   * will result in a no-op.
   *
   * @return a boolean value indicating if starting the tournament was a success.
   */
  suspend fun start(): Boolean

  /** Starts the direct elimination phase of the [Tournament]. */
  suspend fun startDirectElimination()
}

/**
 * Transforms a [TournamentDocument] to a [Tournament].
 *
 * @receiver the [TournamentDocument] that we're transforming.
 * @param user the [AuthenticatedUser] that we see this [Tournament] as.
 * @param store the [Store] used to perform changes.
 * @return the [Tournament] instance.
 */
fun TournamentDocument.toTournament(user: AuthenticatedUser, store: Store): Tournament =
    StoreDocumentTournament(this, user, store)
