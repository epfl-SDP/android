package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.ui.i18n.setContentWithLocalizedStrings
import org.junit.Rule
import org.junit.Test

class ProfileTabBarTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun clickingPastGames_preservesSelection() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val state = rememberProfileTabBarState(0, 0)
          ProfileTabBar(state)
        }

    rule.onNodeWithText(strings.profilePastGames).performClick()
    rule.onNodeWithText(strings.profilePastGames).assertIsSelected()
  }

  @Test
  fun clickingPuzzles_selectsPuzzles() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val state = rememberProfileTabBarState(0, 0)
          ProfileTabBar(state)
        }

    rule.onNodeWithText(strings.profilePuzzle).performClick()
    rule.onNodeWithText(strings.profilePuzzle).assertIsSelected()
  }
}
