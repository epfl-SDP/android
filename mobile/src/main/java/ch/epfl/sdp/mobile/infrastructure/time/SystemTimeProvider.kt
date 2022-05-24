package ch.epfl.sdp.mobile.infrastructure.time

import ch.epfl.sdp.mobile.infrastructure.persistence.store.TimeProvider

/** An object providing the actual current time according to System.currentTimeMillis(). */
object SystemTimeProvider : TimeProvider {
  override fun now(): Long {
    return System.currentTimeMillis()
  }
}
