package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import androidx.datastore.preferences.core.*
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.KeyFactory

/** An object which implements [KeyFactory] and returns [AndroidXKey]. */
object AndroidXKeyFactory : KeyFactory {
  override fun int(name: String) = AndroidXKey(intPreferencesKey(name))
  override fun double(name: String) = AndroidXKey(doublePreferencesKey(name))
  override fun string(name: String) = AndroidXKey(stringPreferencesKey(name))
  override fun boolean(name: String) = AndroidXKey(booleanPreferencesKey(name))
  override fun float(name: String) = AndroidXKey(floatPreferencesKey(name))
  override fun long(name: String) = AndroidXKey(longPreferencesKey(name))
  override fun stringSet(name: String) = AndroidXKey(stringSetPreferencesKey(name))
}
