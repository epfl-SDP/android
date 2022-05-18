package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import androidx.datastore.preferences.core.MutablePreferences as ActualMutablePreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.MutablePreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences

/**
 * An implementation of [MutablePreferences] which uses some [ActualMutablePreferences].
 *
 * @param actual the underlying [ActualMutablePreferences].
 */
class AndroidXMutablePreferences(
    private val actual: ActualMutablePreferences,
) : MutablePreferences, Preferences by AndroidXPreferences(actual) {

  override fun <T> set(key: Key<T>, value: T) = actual.set(key.extractActualKey(), value)

  override fun <T> remove(key: Key<T>) {
    actual.remove(key.extractActualKey())
  }
}
