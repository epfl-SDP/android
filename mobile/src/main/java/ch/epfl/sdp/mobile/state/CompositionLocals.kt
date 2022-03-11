package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import ch.epfl.sdp.mobile.application.AuthenticationFacade

/** A global composition local which provides access to an instance of [AuthenticationFacade]. */
val LocalAuthenticationApi = compositionLocalOf<AuthenticationFacade> { error("Missing API.") }

/**
 * Provides the given APIs through different [androidx.compose.runtime.CompositionLocal] values
 * available throughout the hierarchy.
 *
 * @param authentication the [AuthenticationFacade] that will be provided.
 */
@Composable
fun ProvideApis(
    authentication: AuthenticationFacade,
    content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
      LocalAuthenticationApi provides authentication,
  ) { content() }
}
