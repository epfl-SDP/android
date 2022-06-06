package ch.epfl.sdp.mobile.test.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.authentication.*
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
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.ProvideLocalizedStrings
import ch.epfl.sdp.mobile.test.application.awaitAuthenticatedUser
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import ch.epfl.sdp.mobile.ui.PawniesTheme
import ch.epfl.sdp.mobile.ui.i18n.English
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.filterIsInstance

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

/** An interface representing a standard testing environment. */
interface TestEnvironment {

  /** The [Facades] used. */
  val facades: Facades

  /** The [Infrastructure] used. */
  val infrastructure: Infrastructure

  /** The [LocalizedStrings] applied to the GUI. */
  val strings: LocalizedStrings

  /** The default [AuthenticationUser], which may be authenticated or not. */
  val user: AuthenticationUser

  // Destructuring-friendly syntax.
  operator fun component1(): Facades = facades
  operator fun component2(): Infrastructure = infrastructure
  operator fun component3(): LocalizedStrings = strings
  operator fun component4(): AuthenticationUser = user
}

/**
 * An implementation of [TestEnvironment].
 *
 * @see TestEnvironment
 */
private class ActualTestEnvironment(
    override val facades: Facades,
    override val infrastructure: Infrastructure,
    override val strings: LocalizedStrings,
    override val user: AuthenticationUser,
) : TestEnvironment

/** An interface representing a testing environment with an authenticated user. */
interface AuthenticatedTestEnvironment : TestEnvironment {
  override val user: AuthenticatedUser
  override operator fun component4(): AuthenticatedUser = user
}

/**
 * An implementation of [AuthenticatedTestEnvironment].
 *
 * @param delegate the [TestEnvironment] delegate.
 * @see AuthenticatedTestEnvironment
 */
private class ActualAuthenticatedTestEnvironment(
    delegate: TestEnvironment,
    override val user: AuthenticatedUser,
) : AuthenticatedTestEnvironment, TestEnvironment by delegate {
  override fun component4(): AuthenticatedUser = user
}

/**
 * Creates a [TestEnvironment] using the provided services.
 *
 * @param store the underlying [Store].
 * @param auth the underlying [Auth].
 * @param assets the underlying [AssetManager].
 * @param recognizer the underlying [SpeechRecognizerFactory].
 * @param dataStoreFactory the underlying [DataStoreFactory].
 * @param timeProvider the underlying [TimeProvider].
 *
 * @return a [TestEnvironment] with a disconnected user.
 */
private fun createTestEnvironment(
    store: Store,
    auth: Auth,
    assets: AssetManager,
    recognizer: SpeechRecognizerFactory,
    synthesizer: TextToSpeechFactory = FakeTextToSpeechFactory,
    dataStoreFactory: DataStoreFactory,
    timeProvider: TimeProvider,
): TestEnvironment {
  val authenticationFacade = AuthenticationFacade(auth, store)
  val socialFacade = SocialFacade(auth, store)
  val chessFacade = ChessFacade(auth, store, assets)
  val speechFacade = SpeechFacade(recognizer, synthesizer, dataStoreFactory)
  val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, timeProvider)
  val settingsFacade = SettingsFacade(dataStoreFactory)
  return ActualTestEnvironment(
      facades =
          Facades(
              auth = authenticationFacade,
              chess = chessFacade,
              social = socialFacade,
              speech = speechFacade,
              tournaments = tournamentFacade,
              settings = settingsFacade,
          ),
      infrastructure =
          Infrastructure(
              assets = assets,
              auth = auth,
              dataStoreFactory = dataStoreFactory,
              store = store,
          ),
      // This ignores the language in the dataStoreFactory
      strings = English,
      user = NotAuthenticatedUser,
  )
}

/**
 * Authenticates in the current [TestEnvironment].
 *
 * @receiver the [TestEnvironment] on which authentication is performed.
 * @return the [AuthenticatedTestEnvironment] with the authenticated user.
 */
private suspend fun TestEnvironment.authenticate(): AuthenticatedTestEnvironment {
  val result = facades.auth.signUpWithEmail(DefaultEmail, DefaultName, DefaultPassword)
  check(result == AuthenticationResult.Success) { "Failed authentication in TestEnvironment." }
  val user = facades.auth.awaitAuthenticatedUser()
  return ActualAuthenticatedTestEnvironment(this, user)
}

/**
 * Sets the content with to the [ComposeContentTestRule], and logs a default user in.
 *
 * @param store the [Store] to use by default.
 * @param auth the [Auth] to use by default.
 * @param assets the [AssetManager] to use by default.
 * @param recognizer the [SpeechRecognizerFactory] to use by default.
 * @param dataStoreFactory the [DataStoreFactory] to use by default.
 * @param timeProvider the [TimeProvider] used to calculate the duration of creation of the
 * tournament.
 * @param content the actual composable content to test.
 *
 * @return the [AuthenticatedTestEnvironment] with all the facades, infrastructure and general
 * testing information. The provided [AuthenticatedUser] will be updated whenever the current user
 * profile changes.
 */
suspend fun ComposeContentTestRule.setContentWithAuthenticatedTestEnvironment(
    store: Store = emptyStore(),
    auth: Auth = emptyAuth(),
    assets: AssetManager = emptyAssets(),
    recognizer: SpeechRecognizerFactory = FailingSpeechRecognizerFactory,
    dataStoreFactory: DataStoreFactory = emptyDataStoreFactory(),
    timeProvider: TimeProvider = FakeTimeProvider,
    content: @Composable AuthenticatedTestEnvironment.() -> Unit,
): AuthenticatedTestEnvironment {
  val environment =
      createTestEnvironment(
              store = store,
              auth = auth,
              assets = assets,
              recognizer = recognizer,
              dataStoreFactory = dataStoreFactory,
              timeProvider = timeProvider,
          )
          .authenticate()
  return setContentWithEnvironment(environment) {
    val user by
        remember(environment) {
              environment.facades.auth.currentUser.filterIsInstance<AuthenticatedUser>()
            }
            .collectAsState(environment.user)
    content(
        ActualAuthenticatedTestEnvironment(
            delegate =
                ActualTestEnvironment(
                    facades = environment.facades,
                    infrastructure = environment.infrastructure,
                    strings = environment.strings,
                    user = user,
                ),
            user = user,
        ),
    )
  }
}

/**
 * Sets the content with to the [ComposeContentTestRule], with a default not authenticated user.
 *
 * @param store the [Store] to use by default.
 * @param auth the [Auth] to use by default.
 * @param assets the [AssetManager] to use by default.
 * @param recognizer the [SpeechRecognizerFactory] to use by default.
 * @param dataStoreFactory the [DataStoreFactory] to use by default.
 * @param timeProvider the [TimeProvider] used to calculate the duration of creation of the
 * tournament.
 * @param content the actual composable content to test.
 *
 * @return the [TestEnvironment] with all the facades, infrastructure and general testing
 * information.
 */
fun ComposeContentTestRule.setContentWithTestEnvironment(
    store: Store = emptyStore(),
    auth: Auth = emptyAuth(),
    assets: AssetManager = emptyAssets(),
    recognizer: SpeechRecognizerFactory = FailingSpeechRecognizerFactory,
    dataStoreFactory: DataStoreFactory = emptyDataStoreFactory(),
    timeProvider: TimeProvider = FakeTimeProvider,
    content: @Composable TestEnvironment.() -> Unit,
): TestEnvironment {
  val environment =
      createTestEnvironment(
          store = store,
          auth = auth,
          assets = assets,
          recognizer = recognizer,
          dataStoreFactory = dataStoreFactory,
          timeProvider = timeProvider,
      )
  return setContentWithEnvironment(environment) {
    val user by
        remember(environment) { environment.facades.auth.currentUser }
            .collectAsState(environment.user)
    content(
        ActualTestEnvironment(
            facades = environment.facades,
            infrastructure = environment.infrastructure,
            strings = environment.strings,
            user = user,
        ),
    )
  }
}

/**
 * Sets the content of this [ComposeContentTestRule] with the given [TestEnvironment], providing all
 * the facades.
 *
 * @param E the type of [TestEnvironment].
 * @receiver the [ComposeContentTestRule] on which the content is performed.
 * @param environment the [TestEnvironment] which is used.
 * @param content the body of the content.
 */
private fun <E : TestEnvironment> ComposeContentTestRule.setContentWithEnvironment(
    environment: E,
    content: @Composable E.() -> Unit,
): E {
  setContent {
    PawniesTheme {
      ProvideFacades(
          authentication = environment.facades.auth,
          social = environment.facades.social,
          chess = environment.facades.chess,
          speech = environment.facades.speech,
          tournament = environment.facades.tournaments,
          settings = environment.facades.settings,
      ) { ProvideLocalizedStrings { with(environment) { content() } } }
    }
  }
  return environment
}

/**
 * Wait until a certain text is visible before clicking on it.
 *
 * @param text the expected text.
 * @param timeout the [Duration] for the timeout.
 */
fun ComposeTestRule.performClickOnceVisible(text: String, timeout: Duration = 60.seconds) {
  this.waitUntil(timeoutMillis = timeout.inWholeMilliseconds) {
    onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
  }
  onNodeWithText(text).performClick()
}

// Default values.
private const val DefaultEmail = "alexandre@example.org"
private const val DefaultName = "Alexandre"
private const val DefaultPassword = "hell0hackers!!!!"
