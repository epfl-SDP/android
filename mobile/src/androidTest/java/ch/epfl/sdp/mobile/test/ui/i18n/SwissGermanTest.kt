package ch.epfl.sdp.mobile.test.ui.i18n

import androidx.compose.ui.text.SpanStyle
import ch.epfl.sdp.mobile.ui.i18n.German
import ch.epfl.sdp.mobile.ui.i18n.SwissGerman
import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SwissGermanTest {

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_swissGerman2days_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        SwissGerman.tournamentsStartingTime(2.days, SpanStyle()).equals("Het vor 2 Täg gstartet"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_swissGerman2hours_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        SwissGerman.tournamentsStartingTime(2.hours, SpanStyle())
            .equals("Het vor 2 Stundä gstartet"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_swissGerman2minutes_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        SwissGerman.tournamentsStartingTime(2.minutes, SpanStyle())
            .equals("Het vor 2 Minutä gstartet"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_swissGerman2seconds_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        German.tournamentsStartingTime(2.minutes, SpanStyle())
            .equals("Ist vor 2 Sekundä gestartet"))
  }
}
