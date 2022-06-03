package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade

/** A global composition local which provides access to an instance of [AuthenticationFacade]. */
val LocalAuthenticationFacade =
    compositionLocalOf<AuthenticationFacade> { error("Missing Authentication API.") }

/** A global composition local which provides access to an instance of [SocialFacade]. */
val LocalSocialFacade = compositionLocalOf<SocialFacade> { error("Missing Social API.") }

/** A global composition local which provides access to an instance of [ChessFacade]. */
val LocalChessFacade = compositionLocalOf<ChessFacade> { error("Missing Chess API.") }

/** A global composition local which provides access to an instance of [SpeechFacade]. */
val LocalSpeechFacade = compositionLocalOf<SpeechFacade> { error("Missing Speech Facade.") }

/** A global composition local which provides access to an instance of [TournamentFacade]. */
val LocalTournamentFacade =
    compositionLocalOf<TournamentFacade> { error("Missing Tournament Facade.") }

/** A global composition local which provides access to an instance of [SettingsFacade]. */
val LocalSettingsFacade = compositionLocalOf<SettingsFacade> { error("Missing Settings Facade.") }

/**
 * Provides the given Facades through different [androidx.compose.runtime.CompositionLocal] values
 * available throughout the hierarchy.
 *
 * @param authentication the [AuthenticationFacade] that will be provided.
 * @param social the [SocialFacade] that will be provided.
 * @param chess the [ChessFacade] that will be provided.
 * @param speech the [SpeechFacade] that will be provided.
 * @param tournament the [TournamentFacade] that will be provided.
 * @param settings the [SettingsFacade] that will be provided.
 */
@Composable
fun ProvideFacades(
    authentication: AuthenticationFacade,
    social: SocialFacade,
    chess: ChessFacade,
    speech: SpeechFacade,
    tournament: TournamentFacade,
    settings: SettingsFacade,
    content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
      LocalAuthenticationFacade provides authentication,
      LocalSocialFacade provides social,
      LocalChessFacade provides chess,
      LocalSpeechFacade provides speech,
      LocalTournamentFacade provides tournament,
      LocalSettingsFacade provides settings,
  ) { content() }
}
