package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.androidx

import androidx.datastore.core.DataStore as ActualDataStore
import androidx.datastore.preferences.core.Preferences as ActualPreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXPreferencesDataStore
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AndroidXPreferencesDataStoreTest {

  @Test
  fun given_dataStore_when_callsData_then_callsGoodMethod() = runTest {
    val actualDataStore = mockk<ActualDataStore<ActualPreferences>>()
    val actualPreferences = mockk<ActualPreferences>()
    val dataStore = AndroidXPreferencesDataStore(actualDataStore)

    every { actualDataStore.data } returns flowOf(actualPreferences)

    assertThat(dataStore.data.count()).isEqualTo(1)

    verify { actualDataStore.data }
  }

  @Test
  fun given_dataStore_when_updatesData_then_callsGoodMethod() = runTest {
    val actualDataStore = mockk<ActualDataStore<ActualPreferences>>()
    val actualPreferences = mockk<ActualPreferences>()
    val dataStore = AndroidXPreferencesDataStore(actualDataStore)

    coEvery { actualDataStore.updateData(captureCoroutine()) } coAnswers
        {
          coroutine<suspend (ActualPreferences) -> ActualPreferences>().coInvoke(actualPreferences)
        }

    dataStore.updateData { it }
  }
}
