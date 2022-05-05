package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsProperties.Selected
import androidx.compose.ui.test.*
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.SuspendingAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NavigationTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun loadingSection_isEmpty() {
    val store = emptyStore()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(SuspendingAuth, store)
    val socialFacade = SocialFacade(SuspendingAuth, store)
    val chessFacade = ChessFacade(SuspendingAuth, store, assets)
    rule.setContentWithLocalizedStrings {
      ProvideFacades(facade, socialFacade, chessFacade) { Navigation() }
    }
    rule.onAllNodes(keyIsDefined(SemanticsProperties.Text)).assertCountEquals(0)
  }

  @Test
  fun notAuthenticated_displaysAuthenticationScreen() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(SuspendingAuth, store, assets)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, socialFacade, chessFacade) { Navigation() }
        }

    // Do we see the authentication screen actions ?
    rule.onNodeWithText(strings.authenticatePerformRegister).assertExists()
  }

  @Test
  fun authenticated_displaysHome() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(SuspendingAuth, store, assets)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, socialFacade, chessFacade) { Navigation() }
        }
    facade.signUpWithEmail("email@epfl.ch", "name", "password")

    // Do we see the bottom navigation ?
    rule.onNodeWithText(strings.sectionSocial).assertExists()
    rule.onNodeWithText(strings.sectionSettings).assertExists()
    rule.onNodeWithText(strings.sectionPlay).assertExists()
  }

  @Test
  fun updatingUsername_preservesHomeSection() = runTest {
    val auth = buildAuth { user("email@epfl.ch", "password", "id") }
    val store = buildStore {
      collection("users") { document("id", ProfileDocument(name = "Alice")) }
    }
    val assets = emptyAssets()
    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade) { Navigation() }
        }
    authFacade.signInWithEmail("email@epfl.ch", "password")

    // Move to the profile section.
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(expectValue(Selected, true))

    // Update the username.
    store.collection("users").document("id").update { this["name"] = "Bob" }

    // Check that we're still in the right section.
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(expectValue(Selected, true))
  }
}
