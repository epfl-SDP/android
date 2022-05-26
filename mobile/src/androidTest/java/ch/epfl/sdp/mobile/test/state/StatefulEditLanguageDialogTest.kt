package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.ui.i18n.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulEditLanguageDialogTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_userIsLoggedIn_when_changeLanguageToGerman_then_SettingShouldBeInGerman() = runTest {
    val (_, _, strings) = rule.setContentWithTestEnvironment { Navigation() }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.settingsEditLanguage).performClick()
    rule.onNodeWithText(Language.German.toReadableString()).performClick()
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText(German.settingsEditLanguage).assertIsDisplayed()
    rule.onNodeWithText(German.settingsEditLanguage).performClick()
    rule.onNodeWithText(Language.SwissGerman.toReadableString()).performClick()
    rule.onNodeWithText(German.settingEditSave).performClick()
    rule.onNodeWithText(SwissGerman.settingsEditLanguage).assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_openedChangeLanguageDialog_then_clickCancleButton() = runTest {
    val (_, _, strings) = rule.setContentWithTestEnvironment { Navigation() }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.settingsEditLanguage).performClick()
    rule.onNodeWithText(strings.settingEditCancel).performClick()
    rule.onNodeWithText(English.settingsEditLanguage).assertIsDisplayed()
  }
}
