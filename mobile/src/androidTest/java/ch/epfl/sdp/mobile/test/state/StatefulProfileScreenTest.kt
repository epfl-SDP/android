package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulVisitedProfileScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.twoPuzzleAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import ch.epfl.sdp.mobile.ui.PawniesTheme
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulProfileScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulProfileScreen_when_profileHasPastGames_then_theyAreDisplayedOnScreen() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password", "1") }
      val dataStoreFactory = emptyDataStoreFactory()
      val store = buildStore {
        collection("users") {
          document("1", ProfileDocument("1", name = "A"))
          document("2", ProfileDocument("2", name = "B"))
        }
        collection("games") {
          document(
              "id",
              ChessDocument(uid = "45", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
        }
      }
      val assets = emptyAssets()
      val authFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store, assets)
      val speechFacade =
          SpeechFacade(
              FailingSpeechRecognizerFactory, FakeTextToSpeechFactory, emptyDataStoreFactory())
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)

      authFacade.signUpWithEmail("user1@email", "user1", "password")
      val authUser1 = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              StatefulVisitedProfileScreen(authUser1, "1", {}, {}, {}, {})
            }
          }
      rule.onNodeWithText(strings.profileMatchTitle("B")).assertExists()
    }
  }

  @Test
  fun given_userIsLoggedIn_when_clickedOnChallengeFriend_then_prepareGameDialogWithCorrectlySelectedProfile() =
      runTest {
    val assets = emptyAssets()

    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(uid = "userId2", name = "user2")) }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade =
        SpeechFacade(
            FailingSpeechRecognizerFactory, FakeTextToSpeechFactory, emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val authUser1 = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        socialFacade.profile(uid = "userId2", user = authUser1).filterIsInstance<Profile>().first()
    authUser1.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          PawniesTheme {
            ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
              Navigation()
            }
          }
        }

    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.profileChallenge.uppercase()).performClick()
    rule.onNode(hasText("user2") and hasClickAction()).assertIsSelected()
  }

  @Test
  fun given_userIsLoggedIn_when_clickedOnUnfollowFriend_then_theButtonShouldChangeToFollow() =
      runTest {
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(uid = "userId2", name = "user2")) }
    }

    val (_, _, strings) =
        rule.setContentWithTestEnvironment(store = store) {
          StatefulVisitedProfileScreen(
              user = user,
              uid = "userId2",
              onMatchClick = {},
              onChallengeClick = {},
              onPuzzleClick = {},
              onBack = {})
        }

    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.profileFollow).performClick()
    rule.onNodeWithText(strings.profileUnfollow).assertExists()
    rule.onNodeWithText(strings.profileUnfollow).performClick()
    rule.onNodeWithText(strings.profileFollow).assertExists()
  }

  @Test
  fun given_statefulProfileScreen_when_profileHasSolvedPuzzles_then_theyAreDisplayedOnScreen() =
      runTest {
    val id = "1"
    val (assets, puzzleIds) = twoPuzzleAssets()
    val store = buildStore {
      collection("users") {
        document(id, ProfileDocument(id, solvedPuzzles = listOf(puzzleIds[1])))
      }
    }

    val env =
        rule.setContentWithTestEnvironment(userId = id, store = store, assets = assets) {
          StatefulVisitedProfileScreen(
              user = user,
              uid = id,
              onMatchClick = {},
              onChallengeClick = {},
              onPuzzleClick = {},
              onBack = {})
        }

    rule.onNodeWithText(env.strings.profilePuzzle).performClick()
    rule.onNodeWithText(puzzleIds[1], substring = true).assertExists()
    rule.onNodeWithText(puzzleIds[0], substring = true).assertDoesNotExist()
  }
}
