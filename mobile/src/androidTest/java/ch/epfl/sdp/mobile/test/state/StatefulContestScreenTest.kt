package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulTournamentScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulContestScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulContestScreen_when_tournamentsExist_then_theyAreDisplayedOnTheScreen() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection("tournaments") {
          document("id1", TournamentDocument("tid1", "1", "Tournament 1"))
          document("id2", TournamentDocument("tid2", "2", "Tournament 2"))
        }
      }
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, store)

      val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulTournamentScreen(currentUser, {}, {})
            }
          }
      rule.onNodeWithText("Tournament 1").assertIsDisplayed()
      rule.onNodeWithText(strings.tournamentsBadgeAdmin).assertIsDisplayed()
      rule.onNodeWithText("Tournament 2").assertIsDisplayed()
    }
  }

  @Test
  fun given_statefulContestScreen_when_userJoinedTournament_then_participantBadgeIsDisplayed() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection("tournaments") {
          document("id1", TournamentDocument("tid1", "2", "Tournament 1", playerIds = listOf("1")))
        }
      }
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, store)

      val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulTournamentScreen(currentUser, {}, {})
            }
          }
      rule.onNodeWithText("Tournament 1").assertIsDisplayed()
      rule.onNodeWithText(strings.tournamentsBadgeParticipant).assertIsDisplayed()
    }
  }
}
