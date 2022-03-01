package ch.epfl.sdp.mobile.ui.features.authentication

import androidx.compose.animation.*
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.ui.features.authentication.AuthenticationScreenState.Mode
import ch.epfl.sdp.mobile.ui.features.authentication.AuthenticationScreenState.Mode.*
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings

/**
 * The screen which displays the fields which should be used to authenticate in the app, by creating
 * a new account or by logging in an existing account.
 *
 * @param state the [AuthenticationScreenState], which maintains the composable contents.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun AuthenticationScreen(
    state: AuthenticationScreenState,
    modifier: Modifier = Modifier,
) {
  val transition = updateTransition(state.mode, "Authentication state")
  val strings = LocalLocalizedStrings.current
  Column(modifier) {
    TextField(
        value = state.email,
        onValueChange = { state.email = it },
        label = { Text(strings.authenticateEmailHint) },
    )
    PasswordTextField(
        value = state.password,
        onValueChange = { state.password = it },
        label = { Text(strings.authenticatePasswordHint) },
    )
    val errorText = state.error ?: ""
    AnimatedVisibility(visible = errorText.isNotBlank()) {
      Text(
          text = errorText,
          color = MaterialTheme.colors.error,
          style = MaterialTheme.typography.caption,
      )
    }
    AuthenticateButton(
        transition = transition,
        loading = state.loading,
        onClick = state::onAuthenticate,
    )
    ToggleButton(
        transition = transition,
        onClick = state::toggleMode,
    )
  }
}

@Composable
private fun AuthenticateButton(
    transition: Transition<Mode>,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  LoadingButton(
      loading = loading,
      onClick = onClick,
      modifier = modifier,
  ) {
    transition.AnimatedContent(transitionSpec = { fadeIn() with fadeOut() }) { mode ->
      val text =
          when (mode) {
            LogIn -> LocalLocalizedStrings.current.authenticatePerformLogIn
            Register -> LocalLocalizedStrings.current.authenticatePerformRegister
          }
      Text(text)
    }
  }
}

@Composable
private fun ToggleButton(
    transition: Transition<Mode>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  OutlinedButton(onClick = onClick, modifier = modifier) {
    transition.AnimatedContent(transitionSpec = { fadeIn() with fadeOut() }) { mode ->
      val text =
          when (mode) {
            LogIn -> LocalLocalizedStrings.current.authenticateSwitchToRegister
            Register -> LocalLocalizedStrings.current.authenticateSwitchToLogIn
          }
      Text(text)
    }
  }
}

/** Toggles the [AuthenticationScreenState.mode] from registration to log-in, or vice-versa. */
private fun AuthenticationScreenState.toggleMode() {
  mode =
      when (mode) {
        LogIn -> Register
        Register -> LogIn
      }
}
