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
    }
