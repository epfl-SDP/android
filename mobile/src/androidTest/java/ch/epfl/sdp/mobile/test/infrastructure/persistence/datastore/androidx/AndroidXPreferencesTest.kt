package ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.androidx

import androidx.datastore.preferences.core.MutablePreferences as ActualMutablePreferences
import androidx.datastore.preferences.core.Preferences as ActualPreferences
import androidx.datastore.preferences.core.Preferences.Key as ActualKey
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.Key
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXKeyFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.androidx.AndroidXPreferences
import ch.epfl.sdp.mobile.test.assertThrows
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class AndroidXPreferencesTest {

  @Test
  fun given_preferences_when_contains_then_callsGoodMethods() {
    val actualPreferences = mockk<ActualPreferences>()
    val preferences = AndroidXPreferences(actualPreferences)

    every { actualPreferences.contains(any<ActualKey<Boolean>>()) } returns false

    assertThat(preferences.contains(AndroidXKeyFactory.boolean("hello"))).isFalse()

    verify { actualPreferences.contains(any<ActualKey<Boolean>>()) }
  }

  @Test
  fun given_preferences_when_gets_then_callsGoodMethods() {
    val actualPreferences = mockk<ActualPreferences>()
    val preferences = AndroidXPreferences(actualPreferences)

    every { actualPreferences[any<ActualKey<Boolean>>()] } returns false

    assertThat(preferences[AndroidXKeyFactory.boolean("hello")]).isFalse()

    verify { actualPreferences[any<ActualKey<Boolean>>()] }
  }

  @Test
  fun given_preferences_when_badKeyUsed_then_throws() {
    val actualPreferences = mockk<ActualPreferences>()
    val preferences = AndroidXPreferences(actualPreferences)
    val badKey = object : Key<Int> {} // Not an AndroidXKey -> it should throw !

    assertThrows<IllegalArgumentException> { preferences[badKey] }
  }

  @Test
  fun given_preferences_when_callsToMutablePreferences_then_callsGoodMethods() {
    val actualPreferences = mockk<ActualPreferences>()
    val actualMutablePreferences = mockk<ActualMutablePreferences>()
    val preferences = AndroidXPreferences(actualPreferences)

    every { actualPreferences.toMutablePreferences() } returns actualMutablePreferences

    preferences.toMutablePreferences()

    verify { actualPreferences.toMutablePreferences() }
  }

  @Test
  fun given_preferences_when_callsToPreferences_then_callsGoodMethods() {
    val actualPreferences = mockk<ActualPreferences>()
    val actualPreferencesResult = mockk<ActualMutablePreferences>()
    val preferences = AndroidXPreferences(actualPreferences)

    every { actualPreferences.toPreferences() } returns actualPreferencesResult

    preferences.toPreferences()

    verify { actualPreferences.toPreferences() }
  }
}
