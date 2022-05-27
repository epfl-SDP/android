package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsProperties.Selected
import androidx.compose.ui.test.*
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.SuspendingAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NavigationTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun loadingSection_isEmpty() {
    val store = emptyStore()
    val dataStoreFactory = emptyDataStoreFactory()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(SuspendingAuth, store)
    val socialFacade = SocialFacade(SuspendingAuth, store)
    val chessFacade = ChessFacade(SuspendingAuth, store, assets)
    val speechFacade =
        SpeechFacade(FailingSpeechRecognizerFactory, FakeTextToSpeechFactory,  emptyDataStoreFactory())
    val tournamentFacade =
        TournamentFacade(SuspendingAuth, dataStoreFactory, store, FakeTimeProvider)
    rule.setContentWithLocalizedStrings {
      ProvideFacades(facade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
        Navigation()
      }
    }
    rule.onAllNodes(keyIsDefined(SemanticsProperties.Text)).assertCountEquals(0)
  }

  @Test
  fun notAuthenticated_displaysAuthenticationScreen() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val dataStoreFactory = emptyDataStoreFactory()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(SuspendingAuth, store, assets)
    val speechFacade =
        SpeechFacade(FailingSpeechRecognizerFactory, FakeTextToSpeechFactory,  emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
            Navigation()
          }
        }

    // Do we see the authentication screen actions ?
    rule.onNodeWithText(strings.authenticatePerformRegister).assertExists()
  }

  @Test
  fun authenticated_displaysHome() = runTest {
    val (_, _, strings) = rule.setContentWithTestEnvironment { Navigation() }

    // Do we see the bottom navigation ?
    rule.onNodeWithText(strings.sectionSocial).assertExists()
    rule.onNodeWithText(strings.sectionSettings).assertExists()
    rule.onNodeWithText(strings.sectionPlay).assertExists()
  }

  @Test
  fun updatingUsername_preservesHomeSection() = runTest {
    val (_, _, strings, user) = rule.setContentWithTestEnvironment { Navigation() }

    // Move to the profile section.
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(expectValue(Selected, true))

    // Update the username.
    user.update { name("Bob") }

    // Check that we're still in the right section.
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(expectValue(Selected, true))
  }
}
