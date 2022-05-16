package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.androidx

import androidx.datastore.preferences.core.MutablePreferences as ActualMutablePreferences
import androidx.datastore.preferences.core.Preferences.Key as ActualKey
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXKeyFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXMutablePreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class AndroidMutablePreferencesTest {

  @Test
  fun given_preferences_when_sets_then_callsGoodMethods() {
    val actualPreferences = mockk<ActualMutablePreferences>()
    val preferences = AndroidXMutablePreferences(actualPreferences)

    every { actualPreferences[any<ActualKey<Boolean>>()] = true } returns Unit

    preferences[AndroidXKeyFactory.boolean("hello")] = true

    verify { actualPreferences[any<ActualKey<Boolean>>()] = true }
  }

  @Test
  fun given_preferences_when_remove_then_callsGoodMethods() {
    val actualPreferences = mockk<ActualMutablePreferences>()
    val preferences = AndroidXMutablePreferences(actualPreferences)

    every { actualPreferences.remove(any<ActualKey<Boolean>>()) } returns true

    preferences.remove(AndroidXKeyFactory.boolean("hello"))

    verify { actualPreferences.remove(any<ActualKey<Boolean>>()) }
  }
}
