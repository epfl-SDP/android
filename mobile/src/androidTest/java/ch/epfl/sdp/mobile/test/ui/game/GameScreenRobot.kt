package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * A robot which performs actions on the game screen.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class GameScreenRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /** Asserts that this robot is currently displayed. */
  fun assertIsDisplayed() = chessboard { assertIsDisplayed() }

  /** Asserts that this robot is not currently displayed. */
  fun assertIsNotDisplayed() = chessboard { assertIsNotDisplayed() }

  /**
   * Asserts that the player with the given name is playing.
   *
   * @param name the name of the player.
   */
  fun assertHasPlayer(name: String) {
    onNodeWithText(name).assertIsDisplayed()
  }

  /**
   * Executes the given actions in the scope of the [ChessBoardRobot] for this screen.
   *
   * @param R the return type of the [block].
   * @param block the code to execute.
   */
  inline fun <R> chessboard(
      block: ChessBoardRobot.() -> R,
  ): R = switchTo(::ChessBoardRobot).run(block)
}
