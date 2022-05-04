package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulSettingsScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulSettingScreenTest {
  @get:Rule val rule = createComposeRule()

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
            StatefulSettingsScreen(user, {}, openProfileEditNameMock)
          }
        }

    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()

    assertThat(functionCalled).isTrue()
  }
}
