package ch.epfl.sdp.mobile.state.tournaments

/** Interface providing the current time. */
interface Time {

  /**
   * Function that returns the current time.
   *
   * @returns the current time in [Long] milliseconds
   */
  fun getCurrentTimeMillis(): Long
}
