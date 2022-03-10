package ch.epfl.sdp.mobile.ui.features

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.data.features.authentication.AlwaysSucceedingAuthenticationApi
import ch.epfl.sdp.mobile.data.features.authentication.SuspendingAuthenticationApi
import ch.epfl.sdp.mobile.ui.ProvideApis
import ch.epfl.sdp.mobile.ui.features.authentication.SignUpRobot
import ch.epfl.sdp.mobile.ui.i18n.setContentWithLocalizedStrings
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NavigationTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun loadingSection_isEmpty() {
    rule.setContentWithLocalizedStrings {
      ProvideApis(SuspendingAuthenticationApi) { Navigation() }
    }
    rule.onAllNodes(keyIsDefined(SemanticsProperties.Text)).assertCountEquals(0)
  }

  @Test
  fun notAuthenticated_displaysAuthenticationScreen() = runTest {
    val api = AlwaysSucceedingAuthenticationApi()
    val strings = rule.setContentWithLocalizedStrings { ProvideApis(api) { Navigation() } }

    // Do we see the authentication screen actions ?
    rule.onNodeWithText(strings.authenticatePerformRegister).assertExists()
  }

  @Test
  fun authenticated_displaysHome() = runTest {
    val api = AlwaysSucceedingAuthenticationApi()
    val strings = rule.setContentWithLocalizedStrings { ProvideApis(api) { Navigation() } }
    api.signUpWithEmail("email", "name", "password")

    // Do we see the bottom navigation ?
    rule.onNodeWithText(strings.sectionSocial).assertExists()
    rule.onNodeWithText(strings.sectionSettings).assertExists()
  }

  @Test
  fun authenticatingUser_canSignOut() = runTest {
    val api = AlwaysSucceedingAuthenticationApi()
    val strings = rule.setContentWithLocalizedStrings { ProvideApis(api) { Navigation() } }
    val robot = SignUpRobot(rule, strings)

    robot
        .apply {
          email("alexandre.piveteau@epfl.ch")
          password("password")
        }
        .performSignUp()
        .switchToSettingsSection { performSignOut() }

    rule.onNodeWithText(strings.authenticatePerformRegister).assertIsDisplayed()
  }
}
