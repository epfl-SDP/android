package ch.epfl.sdp.mobile.test.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import ch.epfl.sdp.mobile.test.ui.AbstractRobot
import ch.epfl.sdp.mobile.test.ui.game.GameScreenRobot
import ch.epfl.sdp.mobile.test.ui.prepare_game.PrepareGameRobot
import ch.epfl.sdp.mobile.test.ui.profile.ProfileRobot
import ch.epfl.sdp.mobile.test.ui.profile.SettingsRobot
import ch.epfl.sdp.mobile.test.ui.profile.VisitedProfileRobot
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * A robot which may be used to perform actions on the home-screen, such as navigating across the
 * different home sections.
 *
 * @param rule the underlying [ComposeTestRule].
 * @param strings the [LocalizedStrings] for the composition.
 */
open class HomeSectionRobot(
    rule: ComposeTestRule,
    strings: LocalizedStrings,
) : AbstractRobot(rule, strings) {

  /** Asserts that this section is currently being displayed. */
  open fun assertIsDisplayed() {
    onNode(hasLocalizedText { sectionPlay } and isSelectable()).assertExists()
    onNode(hasLocalizedText { sectionSocial } and isSelectable()).assertExists()
    onNode(hasLocalizedText { sectionContests } and isSelectable()).assertExists()
    onNode(hasLocalizedText { sectionPuzzles } and isSelectable()).assertExists()
    onNode(hasLocalizedText { sectionSettings } and isSelectable()).assertExists()
  }

  /**
   * Switches to the play section.
   *
   * @param block the block to run with the [PlaySectionRobot].
   * @return the [PlaySectionRobot] that should be used.
   */
  inline fun clickPlayTab(
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
  inline fun clickFollowingTab(
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
  inline fun clickTournamentsTab(
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
  inline fun clickSettingsTab(
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
) : HomeSectionRobot(rule, strings) {

  override fun assertIsDisplayed() {
    super.assertIsDisplayed()
    onNode(hasLocalizedText { sectionPlay } and isSelectable()).assertIsSelected()
  }

  /**
   * Switches to the local game screen by clicking the local game action.
   *
   * @param block the block to run with the [GameScreenRobot].
   * @return the [GameScreenRobot] that should be used.
   */
  inline fun clickNewLocalGame(
      block: GameScreenRobot.() -> Unit = {},
  ): GameScreenRobot {
    onNodeWithLocalizedText { newGame }.performClick()
    onNodeWithLocalizedText { prepareGamePlayLocal }.performClick()
    return switchTo(::GameScreenRobot, block)
  }

  /**
   * Switches to the prepare game dialog.
   *
   * @param block the block to run with the [PrepareGameRobot].
   * @return the [PrepareGameRobot] that should be used.
   */
  inline fun clickNewOnlineGame(
      block: PrepareGameRobot.() -> Unit = {},
  ): PrepareGameRobot {
    onNodeWithLocalizedText { newGame }.performClick()
    onNodeWithLocalizedText { prepareGamePlayOnline }.performClick()
    return switchTo(::PrepareGameRobot, block)
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
) : HomeSectionRobot(rule, strings) {

  override fun assertIsDisplayed() {
    super.assertIsDisplayed()
    onNode(hasLocalizedText { sectionSocial } and isSelectable()).assertIsSelected()
  }

  /**
   * Switches to the profile with the given user name.
   *
   * @param name the name of the user whose name is clicked.
   * @param block the block to run with the [ProfileRobot].
   * @return the [VisitedProfileRobot] that should be used.
   */
  inline fun clickProfile(
      name: String,
      block: VisitedProfileRobot.() -> Unit = {},
  ): VisitedProfileRobot {
    waitUntilSuccess { onNodeWithText(name, ignoreCase = true).performClick() }
    return switchTo(::VisitedProfileRobot, block)
  }

  /**
   * Inputs the given [text] in the search field.
   *
   * @param text the text which is searched.
   */
  fun inputSearch(text: String) {
    onNodeWithLocalizedText { socialSearchBarPlaceHolder }.performTextReplacement(text)
  }

  /**
   * Asserts that the profile with the given user name is displayed.
   *
   * @param name the name of the user whose name is checked.
   */
  fun assertProfileIsDisplayed(name: String) {
    waitUntilSuccess { onNodeWithText(name, ignoreCase = true) }.assertIsDisplayed()
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
) : HomeSectionRobot(rule, strings) {

  override fun assertIsDisplayed() {
    super.assertIsDisplayed()
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
) : HomeSectionRobot(rule, strings) {

  override fun assertIsDisplayed() {
    super.assertIsDisplayed()
    onNode(hasLocalizedText { sectionSettings } and isSelectable()).assertIsSelected()
  }

  /** Returns the [ProfileRobot] corresponding to this [SettingsSectionRobot]. */
  fun asProfileRobot(): ProfileRobot {
    // TODO : We may want to refine the hierarchy of robots so SettingsSectionRobot is a
    //        ProfileRobot too.
    return switchTo(::SettingsRobot)
  }
}
