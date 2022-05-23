package ch.epfl.sdp.mobile.state.tournaments

class SystemTime : Time {
  override fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
  }
}
