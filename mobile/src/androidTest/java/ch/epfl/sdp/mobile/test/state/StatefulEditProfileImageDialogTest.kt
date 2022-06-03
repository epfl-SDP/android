package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.ui.setting.Emojis
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulEditProfileImageDialogTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_userIsLoggedIn_when_editProfileImage_then_imageShouldBeUpdated() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { Navigation() }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditImageIcon).performClick()
    rule.onNode(hasText(Emojis[1])).performClick()
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText(Emojis[1]).assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileImage_then_cancelWithoutSave() = runTest {
    val (_, _, strings, user) = rule.setContentWithAuthenticatedTestEnvironment { Navigation() }

    user.update { emoji(Emojis[0]) }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditImageIcon).performClick()
    rule.onNode(hasText(Emojis[1])).performClick()
    rule.onNodeWithText(strings.settingEditCancel).performClick()
    rule.onNodeWithText(Emojis[0]).assertIsDisplayed()
  }
}
