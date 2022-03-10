package ch.epfl.sdp.mobile.data.features.authentication

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthenticatedUserProfileScreenStateTest {

  @Test
  fun correctBehaviour_takesTheUsernameCorrectly() = runTest {
    val mockUser = mockk<AuthenticationApi.User.Authenticated>()
    every { mockUser.name } returns "test"

    val state = AuthenticatedUserProfileScreenState(mockUser)
    assertThat(state.name).isEqualTo("test")
  }
}
