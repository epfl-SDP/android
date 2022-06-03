package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.androidx

import android.content.Context
import androidx.datastore.core.DataStore as ActualDataStore
import androidx.datastore.preferences.core.Preferences as ActualPreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXDataStoreFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AndroidXDataStoreFactoryTest {

  @Test
  fun given_factory_when_createsDataStore_then_callsGoodMethods() = runTest {
    val context = mockk<Context>()
    val actualDatastore = mockk<ActualDataStore<ActualPreferences>>()
    val factory = AndroidXDataStoreFactory(context) { _, _ -> actualDatastore }

    val (store, _) = factory.createPreferencesDataStore()

    every { actualDatastore.data } returns emptyFlow()

    assertThat(store.data.toList()).isEmpty()

    verify { actualDatastore.data }
  }
}
