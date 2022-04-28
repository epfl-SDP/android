package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulProfileScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulProfileScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulProfileScreen_when_profileHasPastGames_then_theyAreDisplayedOnScreen() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val store = buildStore {
        collection("users") {
          document("1", ProfileDocument("1", name = "A"))
          document("2", ProfileDocument("2", name = "B"))
        }
        collection("games") {
          document(
              "id",
              ChessDocument(uid = "45", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
        }
      }
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store)

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade) { StatefulProfileScreen("1") }
          }
      rule.onNodeWithText(strings.profileMatchTitle("B")).assertExists()
    }
  }

  @Test
  fun given_statefulProfileScreen_when_profileStateIsNull_then_aNullProfileIsDisplayed() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "B")) }
      }
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store)
      rule.setContentWithLocalizedStrings {
        ProvideFacades(authFacade, socialFacade, chessFacade) { StatefulProfileScreen("2") }
      }

      rule.onAllNodesWithText("").assertCountEquals(3)
    }
  }
}
