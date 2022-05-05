package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulSettingsScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulSettingsScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulSettingsScreen_when_profileHasPastGames_then_theyAreDisplayedOnScreen() {
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

      authFacade.signInWithEmail("email@example.org", "password")
      val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade) {
              StatefulSettingsScreen(user, {}, {})
            }
          }
      rule.onNodeWithText(strings.profileMatchTitle("B")).assertExists()
    }
  }

  @Test
  fun given_SettingScreenLoaded_when_clickingOnEditProfileName_then_functionShouldBeCalled() =
      runTest {
    val user = mockk<AuthenticatedUser>()
    every { user.name } returns "test"
    every { user.email } returns "test"
    every { user.emoji } returns "test"
    every { user.backgroundColor } returns Profile.Color.Orange
    every { user.uid } returns "test"
    every { user.followed } returns false
    var functionCalled = false

    val openProfileEditNameMock = { functionCalled = true }

    val auth = emptyAuth()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade) {
            StatefulSettingsScreen(user, openProfileEditNameMock, {})
          }
        }

    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()

    Truth.assertThat(functionCalled).isTrue()
  }
}
