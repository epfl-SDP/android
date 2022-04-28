package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulSettingsScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AuthenticatedUserProfileScreenStateTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun correctBehaviour_takesTheUsernameCorrectly() = runTest {
    val mockUser = mockk<AuthenticatedUser>()
    every { mockUser.name } returns "test name"
    every { mockUser.email } returns "test email"
    every { mockUser.emoji } returns "test emoji"
    every { mockUser.uid } returns "test uid"
    every { mockUser.followed } returns false
    val auth = emptyAuth()
    val store = emptyStore()
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val authenticationFacade = AuthenticationFacade(auth, store)

    rule.setContent {
      ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
        StatefulSettingsScreen(mockUser, {})
      }
    }

    rule.onNodeWithText("test name").assertExists()
  }
}
