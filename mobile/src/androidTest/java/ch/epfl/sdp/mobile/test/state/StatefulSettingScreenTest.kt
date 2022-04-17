package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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
    every { user.uid } returns "test"
    every { user.followed } returns false
    var functionCalled = false

    val openProfileEditNameMock = { functionCalled = true }

    rule.setContent { StatefulSettingsScreen(user, openProfileEditNameMock) }

    rule.onNodeWithTag("editProfileName").performClick()

    assertThat(functionCalled).isTrue()
  }
}
