package ch.epfl.sdp.mobile.test.ui.home

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.isSelectable
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
   * Switches to the play section.
   *
   * @param block the block to run with the [PlaySectionRobot].
   * @return the [PlaySectionRobot] that should be used.
   */
  inline fun switchToPlaySection(
      block: PlaySectionRobot.() -> Unit = {},
  ): PlaySectionRobot {
    onNodeWithLocalizedText { sectionPlay }.performClick()
    return switchTo(::PlaySectionRobot, block)
  }

  /**
   * Switches to the following section.
   *
   * @param block the block to run with the [FollowingSectionRobot].
   * @return the [FollowingSectionRobot] that should be used.
   */
  inline fun switchToFollowingSection(
      block: FollowingSectionRobot.() -> Unit = {},
  ): FollowingSectionRobot {
    onNodeWithLocalizedText { sectionSocial }.performClick()
    return switchTo(::FollowingSectionRobot, block)
  }

  /**
   * Switches to the tournaments section.
   *
   * @param block the block to run with the [TournamentsSectionRobot].
   * @return the [TournamentsSectionRobot] that should be used.
   */
  inline fun switchToTournamentsSection(
      block: TournamentsSectionRobot.() -> Unit = {}
  ): TournamentsSectionRobot {
    onNodeWithLocalizedText { sectionContests }.performClick()
    return switchTo(::TournamentsSectionRobot, block)
  }

  /**
   * Switches to the settings section.
   *
   * @param block the block to run with the [SettingsSectionRobot].
   * @return the [SettingsSectionRobot] that should be used.
   */
  inline fun switchToSettingsSection(
      block: SettingsSectionRobot.() -> Unit = {},
  ): SettingsSectionRobot {
    onNodeWithLocalizedText { sectionSettings }.performClick()
    return switchTo(::SettingsSectionRobot, block)
  }
}

/**
 * A robot which can perform some actions on the play section of home.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class PlaySectionRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : HomeRobot(rule, strings) {

  /** Checks that this robot is indeed the one currently displayed on the screen. */
  fun check() {
    onNode(hasLocalizedText { sectionPlay } and isSelectable()).assertIsSelected()
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
) : HomeRobot(rule, strings) {

  /** Checks that this robot is indeed the one currently displayed on the screen. */
  fun check() {
    onNode(hasLocalizedText { sectionSocial } and isSelectable()).assertIsSelected()
  }
}

/**
 * A robot which can perform some actions on the tournaments section of home.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class TournamentsSectionRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : HomeRobot(rule, strings) {

  /** Checks that this robot is indeed the one currently displayed on the screen. */
  fun check() {
    onNode(hasLocalizedText { sectionContests } and isSelectable()).assertIsSelected()
  }
}

/**
 * A robot which can perform some actions on the settings section of home.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
class SettingsSectionRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : HomeRobot(rule, strings) {

  /** Checks that this robot is indeed the one currently displayed on the screen. */
  fun check() {
    onNode(hasLocalizedText { sectionSettings } and isSelectable()).assertIsSelected()
  }
}
