package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.MutablePreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences

/**
 * A fake implementation of [MutablePreferences] which uses a [MutableMap] of values.
 *
 * @param map the underlying [MutableMap] of values.
 */
class FakeMutablePreferences(
    private val map: MutableMap<String, Any?>,
) : MutablePreferences, Preferences by FakePreferences(map) {

  override fun <T> set(key: Key<T>, value: T) = map.set(key.extractActualKey(), value)

  override fun <T> remove(key: Key<T>) {
    map.remove(key.extractActualKey())
  }
}
