package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import android.content.Context
import androidx.datastore.core.DataStore as AndroidXDataStore
import androidx.datastore.preferences.core.Preferences as AndroidXPreferences
import androidx.datastore.preferences.preferencesDataStore
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStore
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStoreFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.KeyFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences
import kotlin.reflect.KProperty

/** The datastore file name. */
private const val DefaultDatastoreFileName = "preferences"

/**
 * An implementation of a [DataStoreFactory] which is based on the AndroidX library.
 *
 * @property context the underlying [Context].
 * @param toDatastore a function which maps the [Context] and the [KProperty] to the actual
 * [AndroidXDataStore] instance. This behavior is provided as a function to allow for mocking.
 */
class AndroidXDataStoreFactory(
    private val context: Context,
    toDatastore: (Context, KProperty<*>) -> AndroidXDataStore<AndroidXPreferences> = { c, p ->
      preferencesDataStore((DefaultDatastoreFileName)).getValue(c, p)
    }
) : DataStoreFactory {

  /** The underlying [AndroidXDataStore]. */
  private val actualDatastore: AndroidXDataStore<AndroidXPreferences> =
      toDatastore(context, this::actualDatastore)

  override fun createPreferencesDataStore(): Pair<DataStore<Preferences>, KeyFactory> =
      AndroidXPreferencesDataStore(actualDatastore) to AndroidXKeyFactory
}
