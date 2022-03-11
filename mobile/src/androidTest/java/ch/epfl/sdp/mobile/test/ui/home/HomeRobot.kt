package ch.epfl.sdp.mobile.test.ui.home

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * A robot which may be used to perform actions on the home-screen, such as navigating across the
 * different home sections.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
abstract class HomeRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /**
   * Switches to the following section.
   *
   * @param block the block to run with the [FollowingSectionRobot].
   * @return the [FollowingSectionRobot] that should be used.
   */
  inline fun switchToFollowingSection(
      block: FollowingSectionRobot.() -> Unit,
  ): FollowingSectionRobot {
    onNodeWithLocalizedText { sectionSocial }.performClick()
    return switchTo(::FollowingSectionRobot, block)
  }

  /**
   * Switches to the settings section.
   *
   * @param block the block to run with the [SettingsSectionRobot].
   * @return the [SettingsSectionRobot] that should be used.
   */
  inline fun switchToSettingsSection(
      block: SettingsSectionRobot.() -> Unit,
  ): SettingsSectionRobot {
    onNodeWithLocalizedText { sectionSettings }.performClick()
    return switchTo(::SettingsSectionRobot, block)
  }
}

/**
 * A robot which can perform some actions on the following section of home.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class FollowingSectionRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : HomeRobot(rule, strings)

/**
 * A robot which can perform some actions on the settings section of home.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class SettingsSectionRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : HomeRobot(rule, strings)
