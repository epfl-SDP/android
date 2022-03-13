package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.test.application.AlwaysSucceedingAuthenticationFacade
import ch.epfl.sdp.mobile.test.application.SuspendingAuthenticationFacade
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NavigationTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun loadingSection_isEmpty() {
    rule.setContentWithLocalizedStrings {
      ProvideFacades(SuspendingAuthenticationFacade) { Navigation() }
    }
    rule.onAllNodes(keyIsDefined(SemanticsProperties.Text)).assertCountEquals(0)
  }

  @Test
  fun notAuthenticated_displaysAuthenticationScreen() = runTest {
    val api = AlwaysSucceedingAuthenticationFacade()
    val strings = rule.setContentWithLocalizedStrings { ProvideFacades(api) { Navigation() } }

    // Do we see the authentication screen actions ?
    rule.onNodeWithText(strings.authenticatePerformRegister).assertExists()
  }

  @Test
  fun authenticated_displaysHome() = runTest {
    val api = AlwaysSucceedingAuthenticationFacade()
    val strings = rule.setContentWithLocalizedStrings { ProvideFacades(api) { Navigation() } }
    api.signUpWithEmail("email", "name", "password")

    // Do we see the bottom navigation ?
    rule.onNodeWithText(strings.sectionSocial).assertExists()
    rule.onNodeWithText(strings.sectionSettings).assertExists()
  }
}
