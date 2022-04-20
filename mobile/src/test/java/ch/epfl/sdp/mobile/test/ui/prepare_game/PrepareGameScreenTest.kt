package ch.epfl.sdp.mobile.test.ui.prepare_game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.state.ProfileAdapter
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState.ColorChoice
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PrepareGameScreenTest {

  private class FakePrepareGameScreenState() : PrepareGameScreenState<ProfileAdapter> {
    override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)
    override val opponents: List<ProfileAdapter> = listOf()
    override var selectedOpponent: ProfileAdapter? = null
    override var playEnabled: Boolean = true
    override fun onPlayClick() {}
    override fun onCancelClick() {}
    override fun onOpponentClick(opponent: ProfileAdapter) {}
  }

  @get:Rule val rule = createComposeRule()

  private fun fakeState(): FakePrepareGameScreenState {
    return FakePrepareGameScreenState()
  }

  @Test
  fun colorChoices_areDisplayed() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists()
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists()
  }

  @Test
  fun chooseColorText_isDisplayed() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameChooseColor).assertExists()
  }

  @Test
  fun chooseOpponentText_isDisplayed() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameChooseOpponent).assertExists()
  }

  @Test
  fun clickingOnBlack_ChangesColorToBlack() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists().performClick()
    assertThat(state.colorChoice).isEqualTo(ColorChoice.Black)
  }

  @Test
  fun clickingOnWhite_ChangesColorToWhite() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists().performClick()
    assertThat(state.colorChoice).isEqualTo(ColorChoice.White)
  }

  @Test
  fun clickingOnBlackThenWhite_ChangesColorToWhite() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists().performClick()
    assertThat(state.colorChoice).isEqualTo(ColorChoice.White)
  }
}
