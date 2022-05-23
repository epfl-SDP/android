package ch.epfl.sdp.mobile.state.tournaments

/** A class that allows changing the time. */
class FakeTime : Time {
  /** The current time that can be modified */
  var currentTime: Long = 1L
    private set

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
