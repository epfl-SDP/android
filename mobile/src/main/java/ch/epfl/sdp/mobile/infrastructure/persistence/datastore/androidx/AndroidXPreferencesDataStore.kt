package ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx

import androidx.datastore.core.DataStore as ActualDataStore
import androidx.datastore.preferences.core.Preferences as ActualPreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStore
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * An implementation of a [DataStore] which uses an [ActualDataStore].
 *
 * @param actual the underlying [ActualDataStore].
 */
class AndroidXPreferencesDataStore(
    private val actual: ActualDataStore<ActualPreferences>,
) : DataStore<Preferences> {

  override val data: Flow<Preferences>
    get() = actual.data.map { AndroidXPreferences(it) }

  override suspend fun updateData(
      transform: suspend (Preferences) -> Preferences,
  ): Preferences =
      AndroidXPreferences(
          actual.updateData { transform(AndroidXPreferences(it)).extractActualPreferences() },
      )
}
