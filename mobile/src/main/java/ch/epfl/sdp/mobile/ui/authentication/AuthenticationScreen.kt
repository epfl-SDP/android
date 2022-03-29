package ch.epfl.sdp.mobile.ui.authentication

import androidx.compose.animation.*
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.R.drawable
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode.LogIn
import ch.epfl.sdp.mobile.ui.authentication.AuthenticationScreenState.Mode.Register

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
  Scaffold(modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(32.dp),
    ) {
      Column(
          verticalArrangement = Arrangement.spacedBy(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Image(painterResource(drawable.authentication_logo), null)
        Text(strings.authenticateTitle, style = MaterialTheme.typography.h4)
        transition.AnimatedContent { target ->
          val subtitle =
              when (target) {
                LogIn -> strings.authenticateSubtitleLogIn
                Register -> strings.authenticateSubtitleRegister
              }
          Text(subtitle, style = MaterialTheme.typography.h6)
        }
      }
      Column {
        TextField(
            value = state.email,
            onValueChange = { state.email = it },
            label = { Text(strings.authenticateEmailHint) },
            modifier = Modifier.fillMaxWidth(),
        )
        transition.AnimatedVisibility(
            visible = { it == Register },
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
          TextField(
              value = state.name,
              onValueChange = { state.name = it },
              label = { Text(strings.authenticateNameHint) },
              modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
          )
        }
        PasswordTextField(
            value = state.password,
            onValueChange = { state.password = it },
            label = { Text(strings.authenticatePasswordHint) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
      }
      val errorText = state.error ?: ""
      AnimatedVisibility(visible = errorText.isNotBlank()) {
        Text(
            text = errorText,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
        )
      }
      Column(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        AuthenticateButton(
            transition = transition,
            loading = state.loading,
            onClick = state::onAuthenticate,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        )
        Text(strings.authenticateOr, style = MaterialTheme.typography.subtitle1)
        ToggleButton(
            transition = transition,
            onClick = state::toggleMode,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        )
      }
    }
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
      shape = RoundedCornerShape(8.dp),
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
  OutlinedButton(
      onClick = onClick,
      modifier = modifier,
      shape = RoundedCornerShape(8.dp),
  ) {
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
