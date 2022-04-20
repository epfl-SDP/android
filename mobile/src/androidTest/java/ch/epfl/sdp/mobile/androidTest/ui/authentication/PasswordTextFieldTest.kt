package ch.epfl.sdp.mobile.androidTest.ui.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.authentication.PasswordTextField
import org.junit.Rule
import org.junit.Test

class PasswordTextFieldTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun default_passwordIsHidden() {
    rule.setContent {
      val (text, setText) = remember { mutableStateOf("") }
      PasswordTextField(text, setText, Modifier.testTag("input"))
    }
    rule.onNodeWithTag("input").performTextInput("Password")
    rule.onNodeWithText("Password").assertDoesNotExist()
  }

  @Test
  fun hiddenPassword_canBeRevealed() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val (text, setText) = remember { mutableStateOf("") }
          PasswordTextField(text, setText, Modifier.testTag("input"))
        }
    rule.onNodeWithTag("input").performTextInput("Password")
    rule.onNodeWithContentDescription(strings.authenticatePasswordToggleVisibility).performClick()
    rule.onNodeWithText("Password").assertExists()
  }

  @Test
  fun default_togglingTwice_isHidden() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val (text, setText) = remember { mutableStateOf("") }
          PasswordTextField(text, setText, Modifier.testTag("input"))
        }
    rule.onNodeWithTag("input").performTextInput("Password")
    repeat(2) {
      rule.onNodeWithContentDescription(strings.authenticatePasswordToggleVisibility).performClick()
    }
    rule.onNodeWithText("Password").assertDoesNotExist()
  }
}
