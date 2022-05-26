package ch.epfl.sdp.mobile.test.ui.i18n

import androidx.compose.ui.text.SpanStyle
import ch.epfl.sdp.mobile.ui.i18n.German
import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GermanTest {

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_german2days_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        German.tournamentsStartingTime(2.days, SpanStyle()).equals("Ist vor 2 Tage gestartet"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_german2hours_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        German.tournamentsStartingTime(2.hours, SpanStyle()).equals("Ist vor 2 Stunden gestartet"))
  }

  @OptIn(ExperimentalTime::class)
  @Test
  fun given_german2minutes_when_exuctingWith2Days_then_shouldReturnCorrectString() = runTest {
    assertThat(
        German.tournamentsStartingTime(2.minutes, SpanStyle())
            .equals("Ist vor 2 Minuten gestartet"))
  }
}
