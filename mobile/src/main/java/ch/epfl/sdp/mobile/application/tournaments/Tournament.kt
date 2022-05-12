package ch.epfl.sdp.mobile.application.tournaments

import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser

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

    /** Indicates that the status is still loading and is not known yet. */
    object Unknown : Status
  }
}

/**
 * Transforms a [TournamentDocument] to a [Tournament].
 *
 * @receiver the [TournamentDocument] that we're transforming.
 * @param user the [AuthenticatedUser] that we see this [Tournament] as.
 * @return the [Tournament] instance.
 */
fun TournamentDocument.toTournament(user: AuthenticatedUser): Tournament =
    object : Tournament {
      override val reference = TournamentReference(this@toTournament.uid ?: "")
      override val name = this@toTournament.name ?: ""
      override val isAdmin = this@toTournament.adminId == user.uid
      override val isParticipant = this@toTournament.playerIds?.contains(user.uid) ?: false

      // TODO : Refine the tournament rules.
      override val status: Tournament.Status =
          Tournament.Status.NotStarted(
              enoughParticipants = (this@toTournament.playerIds?.size
                      ?: 0 >= (this@toTournament.maxPlayers ?: 0)),
          )
    }
