package ch.epfl.sdp.mobile.test.ui.profile

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * An implementation of a robot which makes it possible to interact with a specific user profile.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class ProfileRobot(
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
}
