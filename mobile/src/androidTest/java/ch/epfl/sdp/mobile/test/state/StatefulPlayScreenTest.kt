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
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(userAuthenticated, navigateToGame = {}, onGameItemClick = {})
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("test")).assertExists()
  }

  @Test
  fun given_playerHasLostByCheckmate_when_accessingPlayScreen_then_displayLostByCheckmate() =
      runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection("games") {
        document(
            /* Funfact: Fool's Mate, Fastest checkmate possible https://www.chess.com/article/view/fastest-chess-checkmates */
            "id",
            ChessDocument(
                uid = "786",
                whiteId = "1",
                blackId = "2",
                moves = listOf("f2-f3", "e7-e6", "g2-g4", "Qd8-h4")))
      }
    }

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(userAuthenticated, navigateToGame = {}, onGameItemClick = {})
          }
        }

    rule.onNodeWithText(strings.profileLostByCheckmate(4)).assertExists()
  }

  @Test
  fun given_playerHasWonByCheckmate_when_accessingPlayScreen_then_displayWinByCheckmate() =
      runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection("games") {
        document(
            "id",
            ChessDocument(
                uid = "786",
                whiteId = "2",
                blackId = "1",
                moves = listOf("f2-f3", "e7-e6", "g2-g4", "Qd8-h4")))
      }
    }

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(userAuthenticated, navigateToGame = {}, onGameItemClick = {})
          }
        }

    rule.onNodeWithText(strings.profileWonByCheckmate(4)).assertExists()
  }

  @Test
  fun statefulPlayScreen_isDisplayedWithNoWhiteId() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument("1")) }
      collection("games") {
        document("id", ChessDocument(uid = null, whiteId = null, blackId = "1"))
      }
    }

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(userAuthenticated, navigateToGame = {}, onGameItemClick = {})
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("")).assertExists()
  }

  @Test
  fun statefulPlayScreen_isDisplayedWithNoBlackId() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument("1")) }
      collection("games") {
        document("id", ChessDocument(uid = null, whiteId = "1", blackId = null))
      }
    }

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(userAuthenticated, navigateToGame = {}, onGameItemClick = {})
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("")).assertExists()
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
          ProvideFacades(facade, social, chess) {
            StatefulPlayScreen(user, navigateToGame = {}, onGameItemClick = {})
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("test")).assertDoesNotExist()
  }
}
