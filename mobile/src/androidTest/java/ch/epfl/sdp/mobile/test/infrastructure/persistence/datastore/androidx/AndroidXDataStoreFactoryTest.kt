package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.androidx

import android.content.Context
import androidx.datastore.core.DataStore as ActualDataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences as ActualPreferences
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXDataStoreFactory
import io.mockk.*
import org.junit.Test

class AndroidXDataStoreFactoryTest {

  @Test
  fun given_factory_when_createsDataStore_then_callsGoodMethods() {
    val context = mockk<Context>()
    val factory = AndroidXDataStoreFactory(context)
    val actualDatastore = mockk<ActualDataStore<ActualPreferences>>()
    mockkObject(PreferenceDataStoreFactory)

    every { context.applicationContext } returns context
    every { PreferenceDataStoreFactory.create(any(), any(), any(), any()) } returns actualDatastore

    val (_, _) = factory.createPreferencesDataStore()

    verify { PreferenceDataStoreFactory.create(any(), any(), any(), any()) }
  }
}
