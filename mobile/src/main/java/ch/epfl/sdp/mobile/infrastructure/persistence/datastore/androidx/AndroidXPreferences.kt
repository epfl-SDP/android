package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import androidx.datastore.preferences.core.Preferences as ActualPreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.MutablePreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences

/**
 * An implementation of [Preferences] which uses some [ActualPreferences].
 *
 * @param actual the underlying [AndroidXPreferences].
 */
class AndroidXPreferences(val actual: ActualPreferences) : Preferences {

  override fun <T> contains(key: Key<T>): Boolean = actual.contains(key.extractActualKey())

  override fun <T> get(key: Key<T>): T? = actual[key.extractActualKey()]

  override fun toMutablePreferences(): MutablePreferences =
      AndroidXMutablePreferences(actual.toMutablePreferences())

  override fun toPreferences(): Preferences = AndroidXPreferences(actual.toPreferences())
}

/**
 * Returns the [ActualPreferences] from this [Preferences]. If the preferences are not compatible ,
 * an exception will be thrown.
 *
 * @receiver the [Preferences] from which an [ActualPreferences] is extracted.
 * @return [ActualPreferences] the actual underlying preferences.
 * @throws IllegalArgumentException if the [Preferences] is not an [AndroidXPreferences].
 */
internal fun Preferences.extractActualPreferences(): ActualPreferences =
    requireNotNull((this as? AndroidXPreferences)?.actual) {
      "This Key<*> is incompatible with this DataStore."
    }
