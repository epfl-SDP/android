package ch.epfl.sdp.mobile.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.test.application.awaitAuthenticatedUser
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.ui.PawniesTheme
import ch.epfl.sdp.mobile.ui.i18n.English
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * The lowest-level abstractions with which we can interact during testing.
 *
 * @property assets the [AssetManager] of the app.
 * @property auth the [Auth] of the app.
 * @property store the [Store] of the app.
 */
data class Infrastructure(
    val assets: AssetManager,
    val auth: Auth,
    val store: Store,
)

/**
 * The [Facades] which are available to interact with the data directly.
 *
 * @property auth the backing [AuthenticationFacade].
 * @property chess the backing [ChessFacade].
 * @property social the backing [SocialFacade].
 * @property speech the backing [SpeechFacade].
 * @property tournaments the backing [TournamentFacade].
 */
data class Facades(
    val auth: AuthenticationFacade,
    val chess: ChessFacade,
    val social: SocialFacade,
    val speech: SpeechFacade,
    val tournaments: TournamentFacade,
)

/**
 * A class representing a standard testing environment.
 *
 * @property facades the [Facades] used.
 * @property infrastructure the [Infrastructure] used.
 * @property strings the [LocalizedStrings] applied to the GUI.
 * @property user the default [AuthenticatedUser].
 */
data class TestEnvironment(
    val facades: Facades,
    val infrastructure: Infrastructure,
    val strings: LocalizedStrings,
    val user: AuthenticatedUser,
)

/**
 * Sets the content with to the [ComposeContentTestRule], and logs a default user in.
 *
 * @param store the [Store] to use by default.
 * @param auth the [Auth] to use by default.
 * @param assets the [AssetManager] to use by default.
 * @param recognizer the [SpeechRecognizerFactory] to use by default.
 * @param strings the [LocalizedStrings] for this content.
 * @param content the actual composable content to test.
 *
 * @return the [TestEnvironment] with all the facades, infrastructure and general testing
 * information.
 */
suspend fun ComposeContentTestRule.setContentWithTestEnvironment(
    store: Store = emptyStore(),
    auth: Auth = emptyAuth(),
    assets: AssetManager = emptyAssets(),
    recognizer: SpeechRecognizerFactory = FailingSpeechRecognizerFactory,
    strings: LocalizedStrings = English,
    content: @Composable TestEnvironment.() -> Unit,
): TestEnvironment {
  val authenticationFacade = AuthenticationFacade(auth, store)
  val socialFacade = SocialFacade(auth, store)
  val chessFacade = ChessFacade(auth, store, assets)
  val speechFacade = SpeechFacade(recognizer)
  val tournamentFacade = TournamentFacade(auth, store)
  authenticationFacade.signUpWithEmail(DefaultEmail, DefaultName, DefaultPassword)
  val user = authenticationFacade.awaitAuthenticatedUser()
  val environment =
      TestEnvironment(
          facades =
              Facades(
                  auth = authenticationFacade,
                  chess = chessFacade,
                  social = socialFacade,
                  speech = speechFacade,
                  tournaments = tournamentFacade,
              ),
          infrastructure = Infrastructure(assets = assets, auth = auth, store = store),
          strings = strings,
          user = user,
      )
  setContentWithLocalizedStrings(strings) {
    PawniesTheme {
      ProvideFacades(
          authentication = authenticationFacade,
          social = socialFacade,
          chess = chessFacade,
          speech = speechFacade,
          tournament = tournamentFacade,
          content = { with(environment) { content() } },
      )
    }
  }
  return environment
}

// Default values.
private const val DefaultEmail = "alexandre@example.org"
private const val DefaultName = "Alexandre"
private const val DefaultPassword = "hell0hackers!!!!"