package ch.epfl.sdp.mobile.test.ui.prepare_game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.test.ui.game.ChessBoardRobot
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
  }

  /**
   * Selects the opponent to play against.
   *
   * @param name the name of the opponent who will be selected.
   */
  fun selectOpponent(name: String) {
    waitUntilSuccess { onNodeWithText(name, ignoreCase = true) }.performClick()
  }

  /**
   * Switches to the robot which plays chess games.
   *
   * @param block the body of the [ChessBoardRobot].
   * @return the [ChessBoardRobot] which can be used.
   */
  inline fun switchToGame(block: ChessBoardRobot.() -> Unit): ChessBoardRobot {
    onNodeWithLocalizedText { prepareGamePlay }.performClick()
    return switchTo(::ChessBoardRobot, block)
  }
}
