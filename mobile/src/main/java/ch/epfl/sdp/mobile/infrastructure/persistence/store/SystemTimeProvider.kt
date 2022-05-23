package ch.epfl.sdp.mobile.infrastructure.persistence.store

/** An object providing the actual current time according to System.currentTimeMillis(). */
object SystemTimeProvider : TimeProvider {
  override fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
  }
}
