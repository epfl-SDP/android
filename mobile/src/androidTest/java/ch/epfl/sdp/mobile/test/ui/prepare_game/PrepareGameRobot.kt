package ch.epfl.sdp.mobile.test.ui.prepare_game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.test.ui.game.GameScreenRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * A robot which can perform some actions on the game preparation screen.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class PrepareGameRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /** Asserts that this robot is currently displayed on the screen. */
  fun assertIsDisplayed() {
    onNodeWithLocalizedText { prepareGameChooseOpponent }.assertIsDisplayed()
    onNodeWithLocalizedText { prepareGameBlackColor }.assertIsDisplayed()
    onNodeWithLocalizedText { prepareGameWhiteColor }.assertIsDisplayed()
  }

  /** Asserts that the black color is currently selected. */
  fun assertBlackSelected() {
    onNodeWithLocalizedText { prepareGameBlackColor }.assertIsSelected()
  }

  /** Asserts that the white color is currently selected. */
  fun assertWhiteSelected() {
    onNodeWithLocalizedText { prepareGameWhiteColor }.assertIsSelected()
  }

  /** Presses the button to play as the black player. */
  fun clickPlayAsBlack() {
    onNodeWithLocalizedText { prepareGameBlackColor }.performClick()
  }

  /** Presses the button to play as the white player. */
  fun clickPlayAsWhite() {
    onNodeWithLocalizedText { prepareGameWhiteColor }.performClick()
  }

  /**
   * Selects the opponent to play against.
   *
   * @param name the name of the opponent who will be selected.
   */
  fun selectOpponent(name: String) {
    waitUntilSuccess { onNodeWithText(name, ignoreCase = true) }.performClick()
  }

  /** Presses the cancel action. */
  fun clickCancel() {
    onNodeWithLocalizedText { prepareGameCancel }.performClick()
  }

  /**
   * Switches to the robot which plays chess games.
   *
   * @param block the body of the [GameScreenRobot].
   * @return the [GameScreenRobot] which can be used.
   */
  inline fun clickPlay(block: GameScreenRobot.() -> Unit = {}): GameScreenRobot {
    onNodeWithLocalizedText { prepareGamePlay }.performClick()
    return switchTo(::GameScreenRobot, block)
  }
}
