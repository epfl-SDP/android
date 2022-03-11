package ch.epfl.sdp.mobile.test.ui.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreen
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode.*
import org.junit.Rule
import org.junit.Test

class AuthenticationScreenTest {

  private class SnapshotAuthenticationScreenState : AuthenticationScreenState {
    override var mode: AuthenticationScreenState.Mode by mutableStateOf(Register)
    override var loading: Boolean by mutableStateOf(false)
    override var email: String by mutableStateOf("")
    override var name: String by mutableStateOf("")
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
    rule.onRoot().performTouchInput { swipeUp() }
    val robot = SignUpRobot(rule, strings)
    robot.switchToLogIn {
      onRoot().performTouchInput { swipeUp() }
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
    rule.onRoot().performTouchInput { swipeUp() }
    SignUpRobot(rule, strings)
        .apply {
          email("alexandre.piveteau@epfl.ch")
          name("Alexandre Piveteau")
          password("Password")
        }
        .switchToLogIn()
        .switchToRegister {
          onRoot().performTouchInput { swipeUp() }
          onNodeWithText("alexandre.piveteau@epfl.ch").assertExists()
          onNodeWithText("Alexandre Piveteau").assertExists()
          onNodeWithText("Password").assertExists()
        }
  }
}
