package ch.epfl.sdp.mobile.test.state

import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.application.AuthenticationFacade.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.application.AuthenticationFacade.AuthenticationResult.Success
import ch.epfl.sdp.mobile.state.AuthenticationApiAuthenticationScreenState as AuthApiAuthenticationScreenState
import ch.epfl.sdp.mobile.test.application.SuspendingAuthenticationFacade
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode.*
import ch.epfl.sdp.mobile.ui.i18n.English
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

// TODO : This could be a unit test.
class AuthenticationApiAuthenticationScreenStateTest {

  private fun mockkAll(): Triple<AuthenticationFacade, LocalizedStrings, CoroutineScope> {
    return Triple(mockk(), mockk(), mockk())
  }

  @Test
  fun writtenEmail_isPreserved() {
    val (api, strings, scope) = mockkAll()
    val state = AuthApiAuthenticationScreenState(api, strings, scope)
    state.email = "alexandre.piveteau@epfl.ch"
    assertThat(state.email).isEqualTo("alexandre.piveteau@epfl.ch")
  }

  @Test
  fun writtenPassword_isPreserved() {
    val (api, strings, scope) = mockkAll()
    val state = AuthApiAuthenticationScreenState(api, strings, scope)
    state.password = "securityFTW"
    assertThat(state.password).isEqualTo("securityFTW")
  }

  @Test
  fun defaultMode_isRegister() {
    val (api, strings, scope) = mockkAll()
    val state = AuthApiAuthenticationScreenState(api, strings, scope)
    assertThat(state.mode).isEqualTo(Register)
  }

  @Test
  fun chosenMode_isPreserved() {
    val (api, strings, scope) = mockkAll()
    val state = AuthApiAuthenticationScreenState(api, strings, scope)
    state.mode = LogIn
    assertThat(state.mode).isEqualTo(LogIn)
  }

  @Test
  fun successfulAuthentication_setsLoadingToFalse() = runTest {
    val (api, strings, _) = mockkAll()
    coEvery { api.signUpWithEmail(any(), any(), any()) } returns Success
    val state = AuthApiAuthenticationScreenState(api, strings, this)
    state.onAuthenticate()
    assertThat(state.loading).isFalse()
  }

  @Test
  fun pendingAuthentication_keepsLoadingToTrue() =
      try {
        runTest {
          val state =
              AuthApiAuthenticationScreenState(SuspendingAuthenticationFacade, English, this)
          state.onAuthenticate()
          yield()
          assertThat(state.loading).isTrue()
          advanceUntilIdle()
          cancel()
        }
      } catch (exception: CancellationException) {
        // Success.
      }

  @Test
  fun failingApi_hasErrorMessage() = runTest {
    val api = mockk<AuthenticationFacade>()
    val strings = English
    val state = AuthApiAuthenticationScreenState(api, strings, this)
    coEvery { api.signUpWithEmail(any(), any(), any()) } returns Failure
    state.onAuthenticate()
    yield()
    assertThat(state.error).isEqualTo(strings.authenticateErrorFailure)
  }

  @Test
  fun signInMode_usesSignInApi() = runTest {
    val api = mockk<AuthenticationFacade>(relaxed = true)
    val state = AuthApiAuthenticationScreenState(api, English, this)
    coEvery { api.signInWithEmail(any(), any()) } returns Success
    state.mode = LogIn
    state.onAuthenticate()
    yield()
    coVerify { api.signInWithEmail(any(), any()) }
  }
}
