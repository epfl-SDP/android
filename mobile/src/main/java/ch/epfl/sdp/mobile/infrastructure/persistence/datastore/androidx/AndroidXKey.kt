package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import androidx.datastore.preferences.core.Preferences.Key as ActualKey
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key

/**
 * An implementation of [Key] which uses an [ActualKey] under-the-hood.
 *
 * @param T the type of the value in the [Key].
 * @param actual the actual backing key.
 */
data class AndroidXKey<T>(val actual: ActualKey<T>) : Key<T>

/**
 * Returns the [ActualKey] from this [Key]. If the key is not compatible and was not created for an
 * [AndroidXPreferencesDataStore], an exception will be thrown.
 *
 * @param T the type of the value associated with the key.
 * @receiver the [Key] from which an [ActualKey] is extracted.
 * @return [ActualKey] the actual underlying key.
 * @throws IllegalArgumentException if the [Key] is not an [AndroidXKey].
 */
internal fun <T> Key<T>.extractActualKey(): ActualKey<T> =
    requireNotNull((this as? AndroidXKey<T>)?.actual) {
      "This Key<*> is incompatible with this DataStore."
    }
