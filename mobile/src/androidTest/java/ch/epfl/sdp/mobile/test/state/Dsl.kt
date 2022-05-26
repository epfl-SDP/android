package ch.epfl.sdp.mobile.test.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.assets.AssetManager
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.datastore.DataStoreFactory
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.infrastructure.time.TimeProvider
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.ProvideLocalizedStrings
import ch.epfl.sdp.mobile.test.application.awaitAuthenticatedUser
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.ui.PawniesTheme
import ch.epfl.sdp.mobile.ui.i18n.English
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * The lowest-level abstractions with which we can interact during testing.
 *
 * @property assets the [AssetManager] of the app.
 * @property auth the [Auth] of the app.
 * @property dataStoreFactory the [DataStoreFactory] of the app.
 * @property store the [Store] of the app.
 */
data class Infrastructure(
    val assets: AssetManager,
    val auth: Auth,
    val dataStoreFactory: DataStoreFactory,
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
    val settings: SettingsFacade,
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
 * @param userId the user identifier for the test environment user.
 * @param store the [Store] to use by default.
 * @param auth the [Auth] to use by default.
 * @param assets the [AssetManager] to use by default.
 * @param recognizer the [SpeechRecognizerFactory] to use by default.
 * @param timeProvider the [TimeProvider] used to calculate the duration of creation of the
 * tournament.
 * @param strings the [LocalizedStrings] for this content.
 * @param content the actual composable content to test.
 *
 * @return the [TestEnvironment] with all the facades, infrastructure and general testing
 * information.
 */
suspend fun ComposeContentTestRule.setContentWithTestEnvironment(
    userId: String = DefaultId,
    store: Store = buildStore {
      collection("users") { document(userId, ProfileDocument(uid = userId, name = DefaultName)) }
    },
    auth: Auth = buildAuth { user(DefaultEmail, DefaultPassword, userId) },
    assets: AssetManager = emptyAssets(),
    recognizer: SpeechRecognizerFactory = FailingSpeechRecognizerFactory,
    dataStoreFactory: DataStoreFactory = emptyDataStoreFactory(),
    timeProvider: TimeProvider = FakeTimeProvider,
    content: @Composable TestEnvironment.() -> Unit,
): TestEnvironment {
  val authenticationFacade = AuthenticationFacade(auth, store)
  val socialFacade = SocialFacade(auth, store)
  val chessFacade = ChessFacade(auth, store, assets)
  val speechFacade = SpeechFacade(recognizer)
  val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, timeProvider)
  val settingsFacade = SettingsFacade(dataStoreFactory)
  authenticationFacade.signInWithEmail(DefaultEmail, DefaultPassword)
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
                  settings = settingsFacade),
          infrastructure =
              Infrastructure(
                  assets = assets,
                  auth = auth,
                  dataStoreFactory = dataStoreFactory,
                  store = store,
              ),
          // This ignores the language in the dataStoreFactory
          strings = English,
          user = user,
      )
  setContent {
    PawniesTheme {
      ProvideFacades(
          authentication = authenticationFacade,
          social = socialFacade,
          chess = chessFacade,
          speech = speechFacade,
          tournament = tournamentFacade,
          settings = settingsFacade,
      ) { ProvideLocalizedStrings { with(environment) { content() } } }
    }
  }
  return environment
}

/**
 * Wait until a certain text is visible before clicking on it
 *
 * @param text the expected text
 */
fun ComposeTestRule.performClickOnceVisible(text: String) {
  this.waitUntil { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
  onNodeWithText(text).performClick()
}

// Default values.
private const val DefaultId = "superSU"
private const val DefaultEmail = "alexandre@example.org"
private const val DefaultName = "Alexandre"
private const val DefaultPassword = "hell0hackers!!!!"
