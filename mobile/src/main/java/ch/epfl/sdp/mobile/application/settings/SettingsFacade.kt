package ch.epfl.sdp.mobile.application.settings

import android.content.Context
import android.util.Log
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Represents the facade for the user to modify the global settings
 *
 * @param context The Android [Context] used to set the language
 */
class SettingsFacade(
    private val dataStoreFactory: DataStoreFactory,
) {

  private val Language = "language"

  /** The [DataStore] instance in which the preferences are stored. */
  private val dataStore: DataStore<Preferences>

  /** The key which indicates which indicates what language is selected. */
  private val keyLanguage: Key<String>

  init {
    val (prefs, factory) = dataStoreFactory.createPreferencesDataStore()
    dataStore = prefs
    keyLanguage = factory.string(Language)
  }

  suspend fun setLanguage(language: String) {
      dataStore.edit { it[keyLanguage] = language }
  }

  fun getLanguage(): Flow<String?> {
    return dataStore.data.map {
      Log.i("myinfo", "this one ${it[keyLanguage].let { "lelel" }}")

      it[keyLanguage]

    }
  }
}
