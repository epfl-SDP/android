package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulCreateTournamentScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulCreateTournamentScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultScreen_isDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    facade.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament) {
            StatefulCreateTournamentScreen(user = user, navigateToTournament = {}, cancelClick = {})
          }
        }

    rule.onNodeWithText(strings.tournamentsCreateTitle).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateRules).assertExists()
  }
}
