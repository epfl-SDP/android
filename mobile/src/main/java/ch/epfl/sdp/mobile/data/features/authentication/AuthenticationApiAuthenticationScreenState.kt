package ch.epfl.sdp.mobile.data.features.authentication

import androidx.compose.foundation.MutatePriority.UserInput
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Success
import ch.epfl.sdp.mobile.ui.features.authentication.AuthenticationScreenState
import ch.epfl.sdp.mobile.ui.features.authentication.AuthenticationScreenState.Mode.*
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of the [AuthenticationScreenState] which uses an [AuthenticationApi] to perform
 * authentication requests.
 *
 * @param api the [AuthenticationApi] which is used to make the underlying requests.
 * @param strings the [LocalizedStrings] which provide access to error strings.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class AuthenticationApiAuthenticationScreenState(
    private val api: AuthenticationApi,
    private val strings: LocalizedStrings,
    private val scope: CoroutineScope,
) : AuthenticationScreenState {

  override var mode by mutableStateOf(Register)
  override var loading by mutableStateOf(false)
    private set
  override var email by mutableStateOf("")
  override var password by mutableStateOf("")
  override var error by mutableStateOf<String?>(null)
    private set

  override fun onAuthenticate() {
    scope.launch {
      authenticate {
        val result =
            when (mode) {
              LogIn -> api.signInWithEmail(email, password)
              Register -> api.signUpWithEmail(email, password)
            }
        error =
            when (result) {
              Success -> null
              Failure -> strings.authenticateErrorFailure
            }
      }
    }
  }

  /**
   * A [MutatorMutex] which enforces mutual exclusion of authentication requests. Performing a new
   * request (by clicking the button) will cancel the currently pending request.
   */
  private val mutex = MutatorMutex()

  /**
   * Runs the provided [block] in a suspending fashion, ensuring that the [loading] state is
   * properly set (and reset) before and after the [block] is executed.
   *
   * @param block the block of code which requires mutual exclusion.
   */
  private suspend fun authenticate(block: suspend () -> Unit) {
    mutex.mutate(UserInput) {
      try {
        loading = true
        block()
      } finally {
        loading = false
      }
    }
  }
}
