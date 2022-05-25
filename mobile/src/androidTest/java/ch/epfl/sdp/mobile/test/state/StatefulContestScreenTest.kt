package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.SpanStyle
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.time.system.SystemTimeProvider
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.state.StatefulTournamentScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
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
      val dataStoreFactory = emptyDataStoreFactory()
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection(TournamentDocument.Collection) {
          document("id1", TournamentDocument("tid1", "1", "Tournament 1"))
          document("id2", TournamentDocument("tid2", "2", "Tournament 2"))
        }
      }
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)

      val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulTournamentScreen(currentUser, {}, {}, {})
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
      val dataStoreFactory = emptyDataStoreFactory()
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection(TournamentDocument.Collection) {
          document("id1", TournamentDocument("tid1", "2", "Tournament 1", playerIds = listOf("1")))
        }
      }
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)

      val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulTournamentScreen(currentUser, {}, {}, {})
            }
          }
      rule.onNodeWithText("Tournament 1").assertIsDisplayed()
      rule.onNodeWithText(strings.tournamentsBadgeParticipant).assertIsDisplayed()
    }
  }

  @Test
  fun given_statefulContestScreen_when_tournamentPresent_then_correctTimeDisplayed() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val dataStoreFactory = emptyDataStoreFactory()
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection(TournamentDocument.Collection) {
          document(
              "id1",
              TournamentDocument(
                  "tid1",
                  "2",
                  "Tournament 1",
                  playerIds = listOf("1"),
                  creationTimeEpochMillis = 0L))
        }
      }

      val time = FakeTimeProvider
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, time)

      val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulHome(user)
            }
          }

      rule.onNodeWithText(strings.sectionSocial).performClick()
      val duration = 1.minutes
      time.currentTime = (duration.toLong(DurationUnit.MILLISECONDS))
      rule.onNodeWithText(strings.sectionContests).performClick()
      rule.onNodeWithText(strings.tournamentsStartingTime(duration, SpanStyle()).text)
          .assertIsDisplayed()
      rule.onNodeWithText("Tournament 1").assertIsDisplayed()
    }
  }

  @Test
  fun given_statefulContestScreen_when_tournamentCreated_then_correctTimeDisplayed() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val dataStoreFactory = emptyDataStoreFactory()
      val store = emptyStore()
      val time = FakeTimeProvider
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, time)

      val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulHome(user)
            }
          }

      rule.onNodeWithText(strings.sectionContests).performClick()
      rule.onNodeWithText(strings.newContest).performClick()
      rule.onNodeWithText(strings.tournamentsCreateNameHint).performTextInput("Tournament")
      rule.onNodeWithText("1").performClick()
      rule.onNodeWithText(strings.tournamentsCreateMaximumPlayerHint).performTextInput("4")
      rule.onNodeWithText(strings.tournamentsCreateQualifierSize0).performClick()
      rule.onNodeWithText(strings.tournamentsCreateElimDemomN(2)).performClick()

      rule.onNodeWithText(strings.tournamentsCreateActionCreate).performClick()
      rule.onNodeWithContentDescription(strings.tournamentDetailsBackContentDescription)
          .performClick()
      rule.onNodeWithText(strings.sectionSocial).performClick()

      val duration = 1.minutes
      time.currentTime = (duration.toLong(DurationUnit.MILLISECONDS))

      rule.onNodeWithText(strings.sectionContests).performClick()
      rule.onNodeWithText(strings.tournamentsStartingTime(duration, SpanStyle()).text)
          .assertIsDisplayed()
      rule.onNodeWithText("Tournament").assertIsDisplayed()
      rule.onNodeWithText(strings.tournamentsBadgeAdmin).assertIsDisplayed()
    }
  }

  @Test
  fun given_statefulContestScreen_when_tournamentCreatedHourAgo_then_correctTimeDisplayed() {
    runTest {
      val msHour = 1.hours.toLong(DurationUnit.MILLISECONDS)
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection(TournamentDocument.Collection) {
          document(
              "id1",
              TournamentDocument(
                  "tid1",
                  "2",
                  "Tournament 1",
                  playerIds = listOf("1"),
                  creationTimeEpochMillis = System.currentTimeMillis() - msHour))
        }
      }
      val (_, _, strings) =
          rule.setContentWithTestEnvironment(store = store, timeProvider = SystemTimeProvider) {
            StatefulTournamentScreen(user, {}, {}, {})
          }
      rule.onNodeWithText(strings.tournamentsStartingTime(1.hours, SpanStyle()).text)
          .assertIsDisplayed()
    }
  }

  @Test
  fun given_statefulContestScreen_when_tournamentCreatedDayAgo_then_correctTimeDisplayed() {
    runTest {
      val msDay = 1.days.toLong(DurationUnit.MILLISECONDS)
      val store = buildStore {
        collection("users") { document("1", ProfileDocument("1", name = "A")) }
        collection(TournamentDocument.Collection) {
          document(
              "id1",
              TournamentDocument(
                  "tid1",
                  "2",
                  "Tournament 1",
                  playerIds = listOf("1"),
                  creationTimeEpochMillis = System.currentTimeMillis() - msDay))
        }
      }
      val (_, _, strings) =
          rule.setContentWithTestEnvironment(store = store, timeProvider = SystemTimeProvider) {
            StatefulTournamentScreen(user, {}, {}, {})
          }
      rule.onNodeWithText(strings.tournamentsStartingTime(1.days, SpanStyle()).text)
          .assertIsDisplayed()
    }
  }
}
