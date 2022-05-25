package ch.epfl.sdp.mobile.infrastructure.time

/** Interface providing the current time. */
interface TimeProvider {

  /**
   * Function that returns the current time.
   *
   * @returns the current time in [Long] milliseconds
   */
  fun now(): Long
}
