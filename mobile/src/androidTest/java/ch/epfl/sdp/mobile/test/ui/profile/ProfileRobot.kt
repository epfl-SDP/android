package ch.epfl.sdp.mobile.test.ui.profile

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * An implementation of a robot which makes it possible to interact with a specific user profile.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
abstract class ProfileRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /** Asserts that the profile screen is currently being displayed. */
  fun assertIsDisplayed() {
    onNodeWithLocalizedText { profilePastGames }.assertExists()
    onNodeWithLocalizedText { profilePuzzle }.assertExists()
  }

  /**
   * Asserts that this profile has the given name displayed.
   *
   * @param name the name which should be displayed.
   */
  fun assertHasName(name: String) {
    onNodeWithText(name, ignoreCase = true).assertExists()
  }

  /**
   * Opens the puzzle list and checks that the given puzzle exists.
   *
   * @param name the identifier of the puzzle.
   */
  fun assertHasPuzzle(name: String) {
    onNodeWithLocalizedText { profilePuzzle }.performClick()
    waitUntilSuccess { onNodeWithText(name, ignoreCase = true, substring = true).assertExists() }
  }

  /**
   * Opens the puzzle list and checks that the given puzzle does not exist.
   *
   * @param name the identifier of the puzzle that should be missing.
   */
  fun assertDoesNotHavePuzzle(name: String) {
    onNodeWithLocalizedText { profilePuzzle }.performClick()
    onNodeWithText(name, ignoreCase = true, substring = true).assertDoesNotExist()
  }
}

/**
 * An implementation of a robot which interacts with a visited profile.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class VisitedProfileRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : ProfileRobot(rule, strings) {

  /** Moves to the previous screen by clicking the back icon. */
  fun performBack() {
    onNodeWithLocalizedContentDescription { socialCloseVisitedProfile }.performClick()
  }
}

/**
 * An implementation of a robot which interacts with the user's profile.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class SettingsRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : ProfileRobot(rule, strings)
