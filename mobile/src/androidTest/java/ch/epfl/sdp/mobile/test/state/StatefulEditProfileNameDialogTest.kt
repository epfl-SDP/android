package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.Navigation
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulEditProfileNameDialogTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_nameShouldBeUpdated() = runTest {
    val (_, _, strings, user) = rule.setContentWithAuthenticatedTestEnvironment { Navigation() }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNode(hasText(user.name) and hasSetTextAction()).performTextReplacement("New name")
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText("New name").assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_cancelWithoutSave() = runTest {
    val (_, _, strings, user) = rule.setContentWithAuthenticatedTestEnvironment { Navigation() }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNodeWithText(strings.settingEditCancel).performClick()
    rule.onNodeWithText(user.name).assertIsDisplayed()
  }
}
