package ch.epfl.sdp.mobile.test.state

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.state.AuthenticatedUserProfileScreenState
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthenticatedUserProfileScreenStateTest {

  @Test
  fun correctBehaviour_takesTheUsernameCorrectly() = runTest {
    val mockUser = mockk<AuthenticatedUser>()
    every { mockUser.name } returns "test"
    every { mockUser.email } returns "test"
    every { mockUser.emoji } returns "test"

    val state = AuthenticatedUserProfileScreenState(mockUser)
    assertThat(state.name).isEqualTo("test")
  }
}
