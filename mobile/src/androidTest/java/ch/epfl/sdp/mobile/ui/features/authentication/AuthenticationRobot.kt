package ch.epfl.sdp.mobile.ui.features.authentication

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import ch.epfl.sdp.mobile.ui.features.home.FollowingSectionRobot
import ch.epfl.sdp.mobile.ui.features.robots.AbstractRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * A robot which may be used to perform authentication-related actions.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
abstract class AuthenticationRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /**
   * Sets the email text field.
   *
   * @param text the [String] to set to the email field.
   */
  fun email(text: String) {
    onNodeWithLocalizedText { authenticateEmailHint }.performTextInput(text)
  }

  /**
   * Sets the password text field.
   *
   * @param text the [String] to set to the password field.
   */
  fun password(text: String) {
    onNodeWithLocalizedText { authenticatePasswordHint }.performTextInput(text)
  }
}

/**
 * A robot which will be used to perform sign-in actions.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class SignInRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AuthenticationRobot(rule, strings) {

  init {
    onNodeWithLocalizedText { authenticatePerformLogIn }.assertExists()
    onNodeWithLocalizedText { authenticateSwitchToRegister }.assertExists()
  }

  /**
   * Switches to the sign-up screen.
   *
   * @param block the block to run with the [SignUpRobot].
   * @return the [SignUpRobot] which should be used.
   */
  inline fun switchToRegister(block: SignUpRobot.() -> Unit = {}): SignUpRobot {
    onNodeWithLocalizedText { authenticateSwitchToRegister }.performClick()
    return switchTo(::SignUpRobot, block)
  }

  /**
   * Performs the sign-in action.
   *
   * @param block the block to run on the [FollowingSectionRobot], once the authentication succeeds.
   * @return the [FollowingSectionRobot] which should be used.
   */
  inline fun performSignIn(block: FollowingSectionRobot.() -> Unit = {}): FollowingSectionRobot {
    onNodeWithLocalizedText { authenticatePerformLogIn }.performClick()
    return switchTo(::FollowingSectionRobot, block)
  }
}

/**
 * A robot which will be used to perform sign-up actions.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class SignUpRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AuthenticationRobot(rule, strings) {

  init {
    onNodeWithLocalizedText { authenticatePerformRegister }.assertExists()
    onNodeWithLocalizedText { authenticateSwitchToLogIn }.assertExists()
  }

  /**
   * Sets the name text field.
   *
   * @param name the [String] to set to the name field.
   */
  fun name(name: String) {
    onNodeWithLocalizedText { authenticateNameHint }.performTextInput(name)
  }

  /**
   * Switches to the log-in screen.
   *
   * @param block the block to run with the [SignInRobot].
   * @return the [SignInRobot] which should be used.
   */
  inline fun switchToLogIn(block: SignInRobot.() -> Unit = {}): SignInRobot {
    onNodeWithLocalizedText { authenticateSwitchToLogIn }.performClick()
    return switchTo(::SignInRobot, block)
  }

  /**
   * Performs the sign-up action.
   *
   * @param block the block to run on the [FollowingSectionRobot], once the authentication succeeds.
   * @return the [FollowingSectionRobot] which should be used.
   */
  inline fun performSignUp(block: FollowingSectionRobot.() -> Unit = {}): FollowingSectionRobot {
    onNodeWithLocalizedText { authenticatePerformRegister }.performClick()
    return switchTo(::FollowingSectionRobot, block)
  }
}
