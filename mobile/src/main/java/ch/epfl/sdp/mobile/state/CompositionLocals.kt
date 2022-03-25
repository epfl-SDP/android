package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.online.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade

/** A global composition local which provides access to an instance of [AuthenticationFacade]. */
val LocalAuthenticationFacade =
    compositionLocalOf<AuthenticationFacade> { error("Missing Authentication API.") }

/** A global composition local which provides access to an instance of [SocialFacade]. */
val LocalSocialFacade = compositionLocalOf<SocialFacade> { error("Missing Social API.") }

/** A global composition local which provides access to an instance of [ChessFacade]. */
val LocalChessFacade = compositionLocalOf<ChessFacade> { error("Missing Chess API.") }

/**
 * Provides the given Faces through different [androidx.compose.runtime.CompositionLocal] values
 * available throughout the hierarchy.
 *
 * @param authentication the [AuthenticationFacade] that will be provided.
 * @param social the [SocialFacade] that will be provided.
 */
@Composable
fun ProvideFacades(
    authentication: AuthenticationFacade,
    social: SocialFacade,
    chess: ChessFacade,
    content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
      LocalAuthenticationFacade provides authentication,
      LocalSocialFacade provides social,
      LocalChessFacade provides chess,
  ) { content() }
}
