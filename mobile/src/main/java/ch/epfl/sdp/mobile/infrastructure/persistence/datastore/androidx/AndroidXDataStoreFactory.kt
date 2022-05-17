package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStore
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStoreFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.KeyFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences

/** The datastore file name. */
private const val DefaultDatastoreFileName = "preferences"

/** The actual underlying preferences data store. */
private val Context.dataStore by preferencesDataStore(DefaultDatastoreFileName)

/**
 * An implementation of a [DataStoreFactory] which is based on the AndroidX library.
 *
 * @param context the underlying [Context].
 */
class AndroidXDataStoreFactory(private val context: Context) : DataStoreFactory {

  override fun createPreferencesDataStore(): Pair<DataStore<Preferences>, KeyFactory> =
      AndroidXPreferencesDataStore(context.dataStore) to AndroidXKeyFactory
}
