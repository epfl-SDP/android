package ch.epfl.sdp.mobile.test.ui.setting

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.profile.SettingTabBar
import ch.epfl.sdp.mobile.ui.profile.rememberSettingTabBarState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingTabBarTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun clickingPastGames_preservesSelection() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val state = rememberSettingTabBarState(0, 0)
          SettingTabBar(state)
        }

    rule.onNodeWithText(strings.profilePastGames).performClick()
    rule.onNodeWithText(strings.profilePastGames).assertIsSelected()
  }

  @Test
  fun clickingPuzzles_selectsPuzzles() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val state = rememberSettingTabBarState(0, 0)
          SettingTabBar(state)
        }

    rule.onNodeWithText(strings.profilePuzzle).performClick()
    rule.onNodeWithText(strings.profilePuzzle).assertIsSelected()
  }
}
