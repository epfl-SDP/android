package ch.epfl.sdp.mobile.state.tournaments

/** A class providing the actual current time according to System.currentTimeMillis(). */
class SystemTime : Time {
  override fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
  }
}
