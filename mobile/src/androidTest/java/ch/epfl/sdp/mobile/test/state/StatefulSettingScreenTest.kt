package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.state.StatefulSettingsScreen
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

    val strings =
        rule.setContentWithLocalizedStrings {
          StatefulSettingsScreen(user, openProfileEditNameMock)
        }

    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()

    assertThat(functionCalled).isTrue()
  }
}
