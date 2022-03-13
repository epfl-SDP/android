package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.SuspendingAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NavigationTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun loadingSection_isEmpty() {
    val facade = AuthenticationFacade(SuspendingAuth, emptyStore())
    rule.setContentWithLocalizedStrings { ProvideFacades(facade) { Navigation() } }
    rule.onAllNodes(keyIsDefined(SemanticsProperties.Text)).assertCountEquals(0)
  }

  @Test
  fun notAuthenticated_displaysAuthenticationScreen() = runTest {
    val facade = AuthenticationFacade(emptyAuth(), emptyStore())
    val strings = rule.setContentWithLocalizedStrings { ProvideFacades(facade) { Navigation() } }

    // Do we see the authentication screen actions ?
    rule.onNodeWithText(strings.authenticatePerformRegister).assertExists()
  }

  @Test
  fun authenticated_displaysHome() = runTest {
    val facade = AuthenticationFacade(emptyAuth(), emptyStore())
    val strings = rule.setContentWithLocalizedStrings { ProvideFacades(facade) { Navigation() } }
    facade.signUpWithEmail("email", "name", "password")

    // Do we see the bottom navigation ?
    rule.onNodeWithText(strings.sectionSocial).assertExists()
    rule.onNodeWithText(strings.sectionSettings).assertExists()
  }
}
