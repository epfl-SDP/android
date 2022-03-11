package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.state.StatefulFollowingScreen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class StatefulFollowingScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultMode_displayCorrectFollowers() {
    val mockUser = mockk<AuthenticationFacade.User.Authenticated>()
    every { mockUser.following } returns
        flowOf(
            listOf<AuthenticationFacade.Profile>(
                object : AuthenticationFacade.Profile {
                  override val emoji: String
                    get() = ":>"
                  override val name: String
                    get() = "Hans Peter"
                  override val backgroundColor: ProfileColor
                    get() = ProfileColor.Pink
                }))

    rule.setContent { StatefulFollowingScreen(mockUser) }
    rule.onNodeWithText("Hans Peter").assertExists()
  }
}
