package ch.epfl.sdp.mobile.ui.features.authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.data.features.authentication.AuthenticationApiAuthenticationScreenState
import ch.epfl.sdp.mobile.ui.LocalAuthenticationApi
import ch.epfl.sdp.mobile.ui.i18n.LocalLocalizedStrings

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
