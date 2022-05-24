package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulSettingsScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.twoPuzzleAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.FakeTimeProvider
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulSettingsScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulSettingsScreen_when_profileHasPastGames_then_theyAreDisplayedOnScreen() {
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
      val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
      val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
      val settings = SettingsFacade(dataStoreFactory)

      authFacade.signInWithEmail("email@example.org", "password")
      val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(
                authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
              StatefulSettingsScreen(user, {}, {}, {}, {}, {})
            }
          }
      rule.onNodeWithText(strings.profileMatchTitle("B")).assertExists()
    }
  }

  @Test
  fun given_SettingScreenLoaded_when_clickingOnEditProfileName_then_functionShouldBeCalled() =
      runTest {
    val user = mockk<AuthenticatedUser>()
    every { user.name } returns "test"
    every { user.email } returns "test"
    every { user.emoji } returns "test"
    every { user.backgroundColor } returns Profile.Color.Orange
    every { user.uid } returns "test"
    every { user.followed } returns false
    every { user.solvedPuzzles } returns emptyList()

    var functionCalled = false

    val openProfileEditNameMock = { functionCalled = true }

    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = emptyStore()
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(
              authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
            StatefulSettingsScreen(user, {}, {}, openProfileEditNameMock, {}, {})
          }
        }

    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()

    Truth.assertThat(functionCalled).isTrue()
  }

  @Test
  fun given_statefulSettingsScreen_when_profileHasSolvedPuzzles_then_theyAreDisplayedOnScreen() =
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
          StatefulSettingsScreen(
              user = user,
              onMatchClick = {},
              onPuzzleClick = {},
              onEditProfileImageClick = {},
              onEditProfileNameClick = {},
              onEditLanguageClick = {})
        }

    rule.onNodeWithText(env.strings.profilePuzzle).performClick()
    rule.onNodeWithText(puzzleIds[1], substring = true).assertExists()
    rule.onNodeWithText(puzzleIds[0], substring = true).assertDoesNotExist()
  }

  @Test
  fun given_statefulSettingsScreen_when_logoutButtonClicked_then_disconnectsUser() = runTest {
    val env =
        rule.setContentWithTestEnvironment { StatefulSettingsScreen(user, {}, {}, {}, {}, {}) }
    rule.onNodeWithText(env.strings.settingLogout).assertExists().performClick()
    assertThat(env.facades.auth.currentUser.first()).isEqualTo(NotAuthenticatedUser)
  }
}
