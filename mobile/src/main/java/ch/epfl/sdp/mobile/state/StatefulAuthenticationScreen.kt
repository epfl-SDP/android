package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreen
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A stateful implementation of the [AuthenticationScreen] composable, which uses some
 * composition-local values to retrieve the appropriate dependencies.
 *
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulAuthenticationScreen(
    modifier: Modifier = Modifier,
) {
  val authentication = LocalAuthenticationApi.current
  val strings = LocalLocalizedStrings.current
  val scope = rememberCoroutineScope()
  val state =
      remember(authentication, strings, scope) {
        AuthenticationApiAuthenticationScreenState(authentication, strings, scope)
      }
  AuthenticationScreen(state, modifier)
}

/**
 * An implementation of the [AuthenticationScreenState] which uses an [AuthenticationFacade] to
 * perform authentication requests.
 *
 * @param facade the [AuthenticationFacade] which is used to make the underlying requests.
 * @param strings the [LocalizedStrings] which provide access to error strings.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class AuthenticationApiAuthenticationScreenState(
    private val facade: AuthenticationFacade,
    private val strings: LocalizedStrings,
    private val scope: CoroutineScope,
) : AuthenticationScreenState {

  override var mode by mutableStateOf(AuthenticationScreenState.Mode.Register)
  override var loading by mutableStateOf(false)
    private set
  override var email by mutableStateOf("")
  override var name by mutableStateOf("")
  override var password by mutableStateOf("")
  override var error by mutableStateOf<String?>(null)
    private set

  override fun onAuthenticate() {
    scope.launch {
      authenticate {
        val result =
            when (mode) {
              AuthenticationScreenState.Mode.LogIn -> facade.signInWithEmail(email, password)
              AuthenticationScreenState.Mode.Register ->
                  facade.signUpWithEmail(email, name, password)
            }
        error =
            when (result) {
              AuthenticationFacade.AuthenticationResult.Success -> null
              AuthenticationFacade.AuthenticationResult.Failure -> strings.authenticateErrorFailure
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
    mutex.mutate(MutatePriority.UserInput) {
      try {
        loading = true
        block()
      } finally {
        loading = false
      }
    }
  }
}
