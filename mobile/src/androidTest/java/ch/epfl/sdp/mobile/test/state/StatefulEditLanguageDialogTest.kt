package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.Navigation
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulEditLanguageDialogTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_userIsLoggedIn_when_changeLanguageToGerman_then_SettingShouldBeInGerman() = runTest {
    val (_, _, strings) = rule.setContentWithTestEnvironment { Navigation() }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.settingLanguageLabel).performClick()
    rule.onNodeWithContentDescription("Deutsch").performClick()
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText("Einstellungen").assertIsDisplayed()
  }
}
