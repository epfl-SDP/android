package ch.epfl.sdp.mobile.test.ui.i18n

import androidx.compose.ui.text.SpanStyle
import ch.epfl.sdp.mobile.ui.i18n.French
import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FrenchTest {

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_french2days_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        French.tournamentsStartingTime(2.days, SpanStyle()).equals("Commencé il y a 2 jour(s)"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_french2hours_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        French.tournamentsStartingTime(2.hours, SpanStyle()).equals("Commencé il y a 2 heure(s)"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_french2minutes_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        French.tournamentsStartingTime(2.minutes, SpanStyle())
            .equals("Commencé il y a 2 minute(s)"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_french2seconds_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        French.tournamentsStartingTime(2.seconds, SpanStyle())
            .equals("Commencé il y a 2 second(s)"))
  }
}
