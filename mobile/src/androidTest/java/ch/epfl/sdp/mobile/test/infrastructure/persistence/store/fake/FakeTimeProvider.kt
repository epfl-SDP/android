package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.TimeProvider

/** An object that allows changing the time. */
object FakeTimeProvider : TimeProvider {
  /** The current time that can be modified */
  var currentTime: Long = 0L

  /**
   * Sets the current time to newTime.
   *
   * @param newTime the [Long] newTime.
   */
  fun setTime(newTime: Long) {
    currentTime = newTime
  }

  override fun getCurrentTimeMillis(): Long {
    return currentTime
  }
}
