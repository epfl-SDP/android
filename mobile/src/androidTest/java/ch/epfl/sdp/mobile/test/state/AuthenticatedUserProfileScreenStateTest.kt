package ch.epfl.sdp.mobile.test.state

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.state.AuthenticatedUserProfileScreenState
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthenticatedUserProfileScreenStateTest {

  @Test
  fun correctBehaviour_takesTheUsernameCorrectly() = runTest {
    val mockUser = mockk<AuthenticatedUser>()
    every { mockUser.name } returns "test"
    every { mockUser.email } returns "test"
    every { mockUser.emoji } returns "test"
    every { mockUser.uid } returns "test"
    every { mockUser.followed } returns false
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = ChessFacade(auth, store)
    val job = Job()
    val scope = CoroutineScope(job)

    val state = AuthenticatedUserProfileScreenState(mockUser, facade, scope)
    assertThat(state.name).isEqualTo("test")
  }
}
