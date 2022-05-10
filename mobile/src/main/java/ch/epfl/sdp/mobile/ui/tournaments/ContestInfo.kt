package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.time.Duration

/** An interface which represents the information we may have about a contest. */
@Stable
interface ContestInfo {

  /** The user-readable name of the contest. */
  val name: String

  /** The badge of the contest, if there's any to display. */
  val badge: BadgeType?

  /** The status of the contest. */
  val status: Status

  /** An enumeration which represents the status of a contest. */
  @Immutable
  sealed interface Status {

    /** Indicates that the contest has been open [since] a certain duration. */
    data class Ongoing(val since: Duration) : Status

    /** Indicates that the contest is closed (and therefore can't be joined anymore). */
    object Done : Status
  }
}
