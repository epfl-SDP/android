package ch.epfl.sdp.mobile.ui.tournaments

import kotlin.time.Duration

/** The button to create a new tournament. */
interface Contest {
  val name: String
  val creationTime: Duration
  val personStatus: BadgeType
  val status: Status.ContestStatus
}

sealed interface Status {
  enum class ContestStatus {
    ONGOING,
    DONE
  }
}
