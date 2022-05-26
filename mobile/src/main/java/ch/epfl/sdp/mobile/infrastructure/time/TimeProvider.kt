package ch.epfl.sdp.mobile.infrastructure.time

/** Interface providing the current time. */
interface TimeProvider {

  /**
   * Function that returns the current time.
   *
   * @return the current time in [Long] milliseconds
   */
  fun now(): Long
}
