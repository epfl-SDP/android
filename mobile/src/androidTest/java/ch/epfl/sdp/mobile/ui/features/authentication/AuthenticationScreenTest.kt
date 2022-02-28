package ch.epfl.sdp.mobile.ui.features.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.ui.features.authentication.AuthenticationScreenState.Mode.*
import ch.epfl.sdp.mobile.ui.i18n.setContentWithLocalizedStrings
import org.junit.Rule
import org.junit.Test

class AuthenticationScreenTest {

  private class SnapshotAuthenticationScreenState : AuthenticationScreenState {
    override var mode: AuthenticationScreenState.Mode by mutableStateOf(Register)
    override var loading: Boolean by mutableStateOf(false)
    override var email: String by mutableStateOf("")
    override var password: String by mutableStateOf("")
    override var error: String? by mutableStateOf(null)
    override fun onAuthenticate() {
      loading = !loading
    }
  }

  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultMode_isRegister() {
    val state = SnapshotAuthenticationScreenState()
    val strings = rule.setContentWithLocalizedStrings { AuthenticationScreen(state) }
    val robot = SignUpRobot(rule, strings)
    robot.onNodeWithLocalizedText { authenticatePerformRegister }.assertExists()
  }

  @Test
  fun toggle_switchesToLogIn() {
    val state = SnapshotAuthenticationScreenState()
    val strings = rule.setContentWithLocalizedStrings { AuthenticationScreen(state) }
    val robot = SignUpRobot(rule, strings)
    robot.switchToLogIn {
      onNodeWithLocalizedText { authenticatePerformRegister }.assertDoesNotExist()
    }
  }

  @Test
  fun nonEmptyError_isVisible() {
    val state = SnapshotAuthenticationScreenState()
    val message = "There was an error"
    state.error = message
    rule.setContentWithLocalizedStrings { AuthenticationScreen(state) }
    rule.onNodeWithText(message).assertExists()
  }

  @Test
  fun modeSwitchedTwice_preservesInput() {
    val state = SnapshotAuthenticationScreenState()
    val strings = rule.setContentWithLocalizedStrings { AuthenticationScreen(state) }
    SignUpRobot(rule, strings)
        .apply {
          email("alexandre.piveteau@epfl.ch")
          password("Password")
        }
        .switchToLogIn()
        .switchToRegister {
          onNodeWithText("alexandre.piveteau@epfl.ch").assertExists()
          onNodeWithText("Password").assertExists()
        }
  }
}
