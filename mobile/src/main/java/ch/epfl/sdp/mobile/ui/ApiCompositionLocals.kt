package ch.epfl.sdp.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import ch.epfl.sdp.mobile.data.api.AuthenticationApi

/** A global composition local which provides access to an instance of [AuthenticationApi]. */
val LocalAuthenticationApi = compositionLocalOf<AuthenticationApi> { error("Missing API.") }

/**
 * Provides the given APIs through different [androidx.compose.runtime.CompositionLocal] values
 * available throughout the hierarchy.
 *
 * @param authentication the [AuthenticationApi] that will be provided.
 */
@Composable
fun ProvideApis(
    authentication: AuthenticationApi,
    content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
      LocalAuthenticationApi provides authentication,
  ) { content() }
}
