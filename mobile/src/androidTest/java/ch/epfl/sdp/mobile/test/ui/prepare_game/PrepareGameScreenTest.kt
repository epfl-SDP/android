package ch.epfl.sdp.mobile.test.ui.prepare_game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.ProfileAdapter
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState.ColorChoice
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.hamcrest.core.IsEqual
import org.junit.Rule
import org.junit.Test

class PrepareGameScreenTest {

  private class FakePrepareGameScreenState(
      override val user: AuthenticatedUser,
      override val chessFacade: ChessFacade,
      override val scope: CoroutineScope,
  ) : PrepareGameScreenState<ProfileAdapter> {
    override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)
    override val opponents: List<ProfileAdapter> = listOf()
    override var selectedOpponent: ProfileAdapter? = null
    override var unselectedOpponents: List<ProfileAdapter> = listOf()
    override val navigateToGame: (Match) -> Unit = { _ -> }
    override val onPlayClick: (ProfileAdapter) -> Unit = {}
    override val onCancelClick: () -> Unit = {}
  }

  @get:Rule val rule = createComposeRule()

  private fun fakeState(): FakePrepareGameScreenState {
    val user = mockk<AuthenticatedUser>()
    val chess = mockk<ChessFacade>()
    val scope = mockk<CoroutineScope>()

    return FakePrepareGameScreenState(user, chess, scope)
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
    assertThat(state.colorChoice, IsEqual(ColorChoice.Black))
  }

  @Test
  fun clickingOnWhite_ChangesColorToWhite() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists().performClick()
    assertThat(state.colorChoice, IsEqual(ColorChoice.White))
  }

  @Test
  fun clickingOnBlackThenWhite_ChangesColorToWhite() {
    val state = fakeState()
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists().performClick()
    assertThat(state.colorChoice, IsEqual(ColorChoice.White))
  }
}
