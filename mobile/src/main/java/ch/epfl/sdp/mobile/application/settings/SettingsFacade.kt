package ch.epfl.sdp.mobile.application.settings

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.*
import ch.epfl.sdp.mobile.ui.i18n.Language
import ch.epfl.sdp.mobile.ui.i18n.fromISOStringToLanguage
import ch.epfl.sdp.mobile.ui.i18n.fromLanguageToISOString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Represents the facade for the user to modify the global settings.
 *
 * @property dataStoreFactory a reference to the data store.
 */
class SettingsFacade(
    private val dataStoreFactory: DataStoreFactory,
) {

  /** The datastore keys for the [SettingsFacade]. */
  companion object DatastoreKeys {

    /** The ley for the language key in the settings datastore. */
    private const val Language = "language"
  }

  /** The [DataStore] instance in which the preferences are stored. */
  private val dataStore: DataStore<Preferences>

  /** The key which indicates which indicates what language is selected. */
  private val keyLanguage: Key<String>

  init {
    val (prefs, factory) = dataStoreFactory.createPreferencesDataStore()
    dataStore = prefs
    keyLanguage = factory.string(Language)
  }

  /**
   * Writes a [Language] into the dataStore.
   *
   * @param language The [Language] which should be stored.
   */
  suspend fun setLanguage(language: Language) {
    dataStore.edit { it[keyLanguage] = fromLanguageToISOString(language) }
  }

  /**
   * Get the [Language] which has been in the dataStore.
   *
   * @return The flow of language [Language] which has been stored .
   */
  fun getLanguage(): Flow<Language?> {
    return dataStore.data.map { fromISOStringToLanguage(it[keyLanguage]) }
  }
}
