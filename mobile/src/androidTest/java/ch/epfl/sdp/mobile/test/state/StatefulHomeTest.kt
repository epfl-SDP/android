package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.online.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulHomeTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultSection_isSocial() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    api.signUpWithEmail("email", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickingSettingsTab_selectsSettingsSection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    api.signUpWithEmail("email", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(isSelected())
  }

  @Test
  fun clickSocialSection_selectsSocialSection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    api.signUpWithEmail("email", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickPlaySection_selectsPlaySection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.sectionPlay).assertIsSelected()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
  }

  @Test
  fun clickNewGame_inPlaySection_switchesToBoard() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.sectionPlay).assertIsSelected()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
    rule.onNodeWithText(strings.newGame).assertExists().performClick().assertDoesNotExist()
  }
}
