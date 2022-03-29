package ch.epfl.sdp.mobile.test.ui.prepare_game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.prepare_game.ColorChoice
import ch.epfl.sdp.mobile.ui.prepare_game.GameType
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreen
import ch.epfl.sdp.mobile.ui.prepare_game.PrepareGameScreenState
import org.hamcrest.core.IsEqual
import org.junit.Rule
import org.junit.Test

class PrepareGameScreenTest {

  @get:Rule val rule = createComposeRule()

  val state =
      object : PrepareGameScreenState {
        override var colorChoice: ColorChoice by mutableStateOf(ColorChoice.White)
        override var gameType: GameType by mutableStateOf(GameType.Local)
      }

  @Test
  fun localGameButton_isDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGamePlayLocal).assertExists()
  }

  @Test
  fun onlineGameButton_isDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists()
  }
  @Test
  fun colorChoices_areDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists()
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists()
  }
  @Test
  fun chooseColorText_isDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameChooseColor).assertExists()
  }
  @Test
  fun chooseGameTypeText_isDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameChooseGame).assertExists()
  }
  @Test
  fun clickingOnOnlineGameButton_ChangesToChooseOpponentText() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    assertThat(state.gameType, IsEqual(GameType.Local))
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists().performClick()
    assertThat(state.gameType, IsEqual(GameType.Online))
    rule.onNodeWithText(strings.prepareGameChooseGame).assertDoesNotExist()
    rule.onNodeWithText(strings.prepareGameChooseOpponent).assertExists()
  }
  @Test
  fun clickingOnLocalGameButton_ChangesToChooseGameText() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGamePlayLocal).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameChooseGame).assertExists()
    rule.onNodeWithText(strings.prepareGameChooseOpponent).assertDoesNotExist()
  }
  @Test
  fun clickingOnBlack_ChangesColorToBlack() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists().performClick()
    assertThat(state.colorChoice, IsEqual(ColorChoice.Black))
  }

  @Test
  fun clickingOnWhite_ChangesColorToWhite() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists().performClick()
    assertThat(state.colorChoice, IsEqual(ColorChoice.White))
  }

  @Test
  fun clickingLocalGame_ChangesGameTypeToLocal() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGamePlayLocal).assertExists().performClick()
    assertThat(state.gameType, IsEqual(GameType.Local))
  }

  @Test
  fun clickingOnlineGame_ChangesGameTypeToOnline() {
    val strings = rule.setContentWithLocalizedStrings { PrepareGameScreen(state) }
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists().performClick()
    assertThat(state.gameType, IsEqual(GameType.Online))
  }
}
