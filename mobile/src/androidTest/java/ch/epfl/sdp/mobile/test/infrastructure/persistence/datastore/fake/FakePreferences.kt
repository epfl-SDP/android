@file:Suppress("UNCHECKED_CAST")

package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.MutablePreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences

/**
 * A fake implementation of [Preferences] which uses a [Map] of values.
 *
 * @param map the underlying [Map] of values.
 */
class FakePreferences(private val map: Map<String, Any?>) : Preferences {

  override fun <T> contains(key: Key<T>) = map.contains(key.extractActualKey())

  override fun <T> get(key: Key<T>): T? = map[key.extractActualKey()] as? T

  override fun toMutablePreferences(): MutablePreferences =
      FakeMutablePreferences(map.toMutableMap())

  override fun toPreferences(): Preferences = FakePreferences(map.toMap())
}

/**
 * Returns the [FakePreferences] from this [Preferences]. If the preferences are not compatible , an
 * exception will be thrown.
 *
 * @receiver the [Preferences] from which a [FakePreferences] is extracted.
 * @return [FakePreferences] the actual underlying preferences.
 * @throws IllegalArgumentException if the [Preferences] is not some [FakePreferences].
 */
internal fun Preferences.extractActualPreferences(): FakePreferences =
    requireNotNull(this as? FakePreferences) { "This Key<*> is incompatible with this DataStore." }
