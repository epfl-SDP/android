package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulPlayScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPlayScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun statefulPlayScreen_isDisplayed() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection("games") {
        document("id", ChessDocument(uid = "786", whiteId = "1", blackId = "2"))
      }
    }

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPlayScreen(userAuthenticated, {}) }
        }

    rule.onNodeWithText(strings.profileMatchTitle("test")).assertExists()
  }

  @Test
  fun statefulPlayScreen_isNotDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signUpWithEmail("email@example.org", "test", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) { StatefulPlayScreen(user, {}) }
        }

    rule.onNodeWithText(strings.profileMatchTitle("test")).assertDoesNotExist()
  }
}
