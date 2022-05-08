package ch.epfl.sdp.mobile.ui.tournaments

import kotlin.time.Duration

interface Contest {
  val name: String
  val creationDate: Duration
  val personStatus: ContestPersonStatus
  val status: ContestStatus
}

enum class ContestPersonStatus {
  ADMIN,
  PARTICIPANT,
  VIEWER
}

enum class ContestStatus {
  ONGOING,
  DONE
}
