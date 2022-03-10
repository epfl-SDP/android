package ch.epfl.sdp.mobile.ui.features.social

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.ProfileColor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import org.junit.Rule
import org.junit.Test

class StatefulFollowingScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultMode_displayCorrectFollowers() {
    val mockUser = mockk<AuthenticationApi.User.Authenticated>()
    every { mockUser.following } returns
        flow {
          emit(
              listOf<AuthenticationApi.Profile>(
                  object : AuthenticationApi.Profile {
                    override val emoji: String
                      get() = ":>"
                    override val name: String
                      get() = "Hans Peter"
                    override val backgroundColor: ProfileColor
                      get() = ProfileColor.Pink
                  }))
        }
    rule.setContent { StatefulFollowingScreen(mockUser) }
    rule.onNodeWithText("Hans Peter").assertExists()
  }
}
