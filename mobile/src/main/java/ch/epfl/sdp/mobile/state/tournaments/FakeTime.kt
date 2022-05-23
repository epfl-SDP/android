package ch.epfl.sdp.mobile.state.tournaments

class FakeTime : Time {
  var currentTime = 0L

  override fun getCurrentTimeMillis(): Long {
    return currentTime
  }
}
