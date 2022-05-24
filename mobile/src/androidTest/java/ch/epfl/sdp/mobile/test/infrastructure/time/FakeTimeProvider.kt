package ch.epfl.sdp.mobile.test.infrastructure.time

import ch.epfl.sdp.mobile.infrastructure.persistence.store.TimeProvider

/** An object that allows changing the time. */
object FakeTimeProvider : TimeProvider {
  /** The current time that can be modified */
  var currentTime: Long = 0L

  override fun now(): Long {
    return currentTime
  }
}
