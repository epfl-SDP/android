package ch.epfl.sdp.mobile.test.ui.play

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

open class TestPlayScreenState() : PlayScreenState {
  override val user: AuthenticatedUser = mockk()
  override fun onLocalGameClick() {}
  override fun onOnlineGameClick() {}
}

object FakePlayScreenState : TestPlayScreenState()

class PlayScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun newGame_isDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PlayScreen(FakePlayScreenState) }
    rule.onNodeWithText(strings.newGame).assertExists()
  }
}
