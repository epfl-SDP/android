package ch.epfl.sdp.mobile.androidTest.ui.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.sharedTest.ui.authentication.SignUpRobot
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulAuthenticationScreen
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreen
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
    val robot = SignUpRobot(rule, strings)
    robot.switchToLogIn {
      onNodeWithLocalizedText { authenticatePerformRegister }.assertDoesNotExist()
    }
  }

  @Test
  fun given_authenticationSignUpScreen_when_badPasswordIsInserted_then_aFailureBadPasswordMessageAppears() {
    val auth = emptyAuth()
    val store = emptyStore()
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
            StatefulAuthenticationScreen()
          }
        }
    val robot = SignUpRobot(rule, strings)
    robot.email("a@epfl.ch")
    robot.password("weak")
    robot.performSignUp()
    rule.onNodeWithText(strings.authenticateBadPasswordFailure).assertExists()
  }

  @Test
  fun given_authenticationSignUpScreen_when_invalidEmailIsInserted_then_aFailureInvalidEmailFormatMessageAppears() {
    val auth = emptyAuth()
    val store = emptyStore()
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
            StatefulAuthenticationScreen()
          }
        }
    val robot = SignUpRobot(rule, strings)
    robot.email("a")
    robot.password("password")
    robot.performSignUp()
    rule.onNodeWithText(strings.authenticateWrongEmailFormatFailure).assertExists()
  }

  @Test
  fun given_authenticationSignUpScreen_when_signUpWithExistingEmailOccurs_then_aFailureExistingAccountMessageAppears() {
    val auth = buildAuth { user("fouad.mahmoud@epfl.ch", "password") }
    val store = emptyStore()
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
            StatefulAuthenticationScreen()
          }
        }
    val robot = SignUpRobot(rule, strings)
    robot.email("fouad.mahmoud@epfl.ch")
    robot.password("password")
    robot.performSignUp()
    rule.onNodeWithText(strings.authenticateExistingAccountFailure).assertExists()
  }

  @Test
  fun given_authenticationSignInScreen_when_incorrectUserPasswordIsInserted_then_aFailureIncorrectPasswordMessageAppears() {
    val auth = buildAuth { user("fouad.mahmoud@epfl.ch", "password") }
    val store = emptyStore()
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
            StatefulAuthenticationScreen()
          }
        }
    val robot = SignUpRobot(rule, strings).switchToLogIn()
    robot.email("fouad.mahmoud@epfl.ch")
    robot.password("wrong")
    robot.performSignIn()
    rule.onNodeWithText(strings.authenticateIncorrectPasswordFailure).assertExists()
  }

  @Test
  fun given_authenticationSignInScreen_when_unregisteredEmailIsTyped_then_aFailureInvalidUserMessageAppears() {
    val auth = emptyAuth()
    val store = emptyStore()
    val authenticationFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
            StatefulAuthenticationScreen()
          }
        }
    val robot = SignUpRobot(rule, strings).switchToLogIn()
    robot.email("fouad.mahmoud@epfl.ch")
    robot.password("password")
    robot.performSignIn()
    rule.onNodeWithText(strings.authenticateInvalidUserFailure).assertExists()
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
          name("Alexandre Piveteau")
          password("Password")
        }
        .switchToLogIn()
        .switchToRegister {
          onNodeWithText("alexandre.piveteau@epfl.ch").assertExists()
          onNodeWithText("Alexandre Piveteau").assertExists()
          onNodeWithText("Password").assertExists()
        }
  }
}
