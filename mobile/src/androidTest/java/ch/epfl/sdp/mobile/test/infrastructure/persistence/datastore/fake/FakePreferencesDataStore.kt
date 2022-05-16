package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStore
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** An implementation of [DataStore] which uses a [MutableStateFlow] to implement observability. */
class FakePreferencesDataStore : DataStore<Preferences> {

  /** The [MutableStateFlow] which backs these fake preferences. */
  private val current = MutableStateFlow(FakePreferences(emptyMap()))

  /** The [Mutex] which ensures mutually exclusive access to the [current] preferences.l */
  private val mutex = Mutex()

  override val data: Flow<Preferences>
    get() = current

  override suspend fun updateData(
      transform: suspend (Preferences) -> Preferences,
  ): Preferences =
      mutex.withLock {
        transform(current.value).extractActualPreferences().also { current.value = it }
      }
}
