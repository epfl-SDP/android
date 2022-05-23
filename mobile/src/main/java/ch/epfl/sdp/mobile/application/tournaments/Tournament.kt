package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.state.tournaments.SystemTime
import ch.epfl.sdp.mobile.state.tournaments.Time
import kotlin.time.Duration

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

  /** The duration from when the tournament was created. */
  val durationCreated: Duration

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

    /**
     * Indicates that we are in a direct elimination phase, with a [List] of current [Round].
     *
     * @param rounds the [List] of all the [Round].
     */
    data class DirectElimination(val rounds: List<Round>) : Status

    /**
     * An elimination round.
     *
     * @param name the name of the elimination round.
     * @param depth the depth of the elimination round.
     * @param moveToNextRoundEnabled true iff moving to the next round is possible on this round.
     */
    data class Round(
        val name: String,
        val depth: Int,
        val moveToNextRoundEnabled: Boolean,
    )

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

  /** Starts the next direct elimination round of the [Tournament]. */
  suspend fun startNextRound()
}

/**
 * Transforms a [TournamentDocument] to a [Tournament].
 *
 * @receiver the [TournamentDocument] that we're transforming.
 * @param user the [AuthenticatedUser] that we see this [Tournament] as.
 * @param store the [Store] used to perform changes.
 * @param time the [Time] used to calculate the duration of creation of the tournament.
 * @return the [Tournament] instance.
 */
fun TournamentDocument.toTournament(user: AuthenticatedUser, store: Store, time: Time = SystemTime()): Tournament =
    StoreDocumentTournament(this, user, store, time)
