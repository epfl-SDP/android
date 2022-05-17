package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key

/**
 * An implementation of a [Key] which has a [name].
 *
 * @param name the name of the key.
 */
data class FakeKey<T>(val name: String) : Key<T>

/**
 * Returns the [String] from this [Key]. If the key is not compatible and was not created for a
 * [FakePreferencesDataStore], an exception will be thrown.
 *
 * @param T the type of the value associated with the key.
 * @receiver the [Key] from which a [String] key is extracted.
 * @return [String] the actual underlying key.
 * @throws IllegalArgumentException if the [Key] is not a [FakeKey].
 */
internal fun <T> Key<T>.extractActualKey(): String =
    requireNotNull((this as? FakeKey<T>)?.name) {
      "This Key<*> is incompatible with this DataStore."
    }
