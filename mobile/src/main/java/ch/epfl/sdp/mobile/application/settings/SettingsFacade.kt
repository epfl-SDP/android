package ch.epfl.sdp.mobile.application.settings

import android.content.Context
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXDataStoreFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.edit
import kotlinx.coroutines.flow.first

/**
 * Represents the facade for the user to modify the global settings
 *
 * @param context The Android [Context] used to set the language
 */
class SettingsFacade(
  private val context: Context,
  ) {

  suspend fun setLanguage(language: String) {
    val (store, factory) = AndroidXDataStoreFactory(context).createPreferencesDataStore()
    val key = factory.string("language")
    store.edit { it[key] = language }
  }

  suspend fun getLanguage(): String? {
    val (store, factory) = AndroidXDataStoreFactory(context).createPreferencesDataStore()
    val key = factory.string("language")
    return store.data.first()[key]
  }
}