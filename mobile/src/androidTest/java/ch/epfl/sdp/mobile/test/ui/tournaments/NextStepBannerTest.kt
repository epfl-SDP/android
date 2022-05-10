package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.ui.tournaments.GreenNextStepBanner
import ch.epfl.sdp.mobile.ui.tournaments.OrangeNextStepBanner
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import org.junit.Rule
import org.junit.Test

class NextStepBannerTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_greenBanner_when_clicking_then_callsCallback() {
    val channel = Channel<Unit>(1)
    rule.setContent {
      GreenNextStepBanner(
          title = "hello",
          message = "there",
          onClick = { channel.trySend(Unit) },
      )
    }
    rule.onNodeWithText("hello").performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_orangeBanner_when_clicking_then_callsCallback() {
    val channel = Channel<Unit>(1)
    rule.setContent {
      OrangeNextStepBanner(
          title = "hello",
          message = "there",
          onClick = { channel.trySend(Unit) },
      )
    }
    rule.onNodeWithText("hello").performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }
}
