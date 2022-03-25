package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.online.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulPrepareGameScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPrepareGameScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultScreen_isDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPrepareGameScreen(user) }
        }

    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists()
    rule.onNodeWithText(strings.prepareGameChooseGame).assertExists()
    rule.onNodeWithText(strings.prepareGameChooseColor).assertExists()
  }

  @Test
  fun switchColorToBlack_works() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPrepareGameScreen(user) }
        }

    rule.onNodeWithText(strings.prepareGameChooseColor).assertExists()
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists().performClick()
  }

  @Test
  fun switchColorBackToWhite_works() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPrepareGameScreen(user) }
        }

    rule.onNodeWithText(strings.prepareGameChooseColor).assertExists()
    rule.onNodeWithText(strings.prepareGameBlackColor).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameWhiteColor).assertExists().performClick()
  }

  @Test
  fun switchGameToOnline_works() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPrepareGameScreen(user) }
        }
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameChooseOpponent).assertExists()
  }

  @Test
  fun switchGameToOffline_works() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPrepareGameScreen(user) }
        }
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameChooseOpponent).assertExists()
    rule.onNodeWithText(strings.prepareGamePlayLocal).assertExists().performClick()
    rule.onNodeWithText(strings.prepareGameChooseOpponent).assertDoesNotExist()
    rule.onNodeWithText(strings.prepareGameChooseGame).assertExists()
  }
}
