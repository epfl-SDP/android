package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulPlayScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.sound.fake.FakeSoundPlayer
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import com.google.common.truth.Truth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPlayScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun statefulPlayScreen_isDisplayed() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection(ChessDocument.Collection) {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = userAuthenticated,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("test")).assertExists()
  }

  @Test
  fun given_playerHasLostByCheckmate_when_accessingPlayScreen_then_displayLostByCheckmate() =
      runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection(ChessDocument.Collection) {
        document(
            /* Funfact: Fool's Mate, Fastest checkmate possible https://www.chess.com/article/view/fastest-chess-checkmates */
            "id",
            ChessDocument(
                uid = "786",
                whiteId = "1",
                blackId = "2",
                moves = listOf("f2-f3", "e7-e6", "g2-g4", "Qd8-h4")))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = userAuthenticated,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.profileLostByCheckmate(4)).assertExists()
  }

  @Test
  fun given_playerHasWonByCheckmate_when_accessingPlayScreen_then_displayWinByCheckmate() =
      runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection(ChessDocument.Collection) {
        document(
            "id",
            ChessDocument(
                uid = "786",
                whiteId = "2",
                blackId = "1",
                moves = listOf("f2-f3", "e7-e6", "g2-g4", "Qd8-h4")))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = userAuthenticated,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.profileWonByCheckmate(4)).assertExists()
  }

  @Test
  fun statefulPlayScreen_isDisplayedWithNoWhiteId() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) { document("1", ProfileDocument("1")) }
      collection(ChessDocument.Collection) {
        document("id", ChessDocument(uid = null, whiteId = null, blackId = "1"))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = userAuthenticated,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("")).assertExists()
  }

  @Test
  fun statefulPlayScreen_isDisplayedWithNoBlackId() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) { document("1", ProfileDocument("1")) }
      collection(ChessDocument.Collection) {
        document("id", ChessDocument(uid = null, whiteId = "1", blackId = null))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signInWithEmail("email@example.org", "password")
    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = userAuthenticated,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("")).assertExists()
  }

  @Test
  fun statefulPlayScreen_isNotDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val dataStoreFactory = emptyDataStoreFactory()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("email@example.org", "test", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = user,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.profileMatchTitle("test")).assertDoesNotExist()
  }

  @Test
  fun given_playScreen_when_clickingNewGame_then_localAndOnlinePlayAreDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val dataStoreFactory = emptyDataStoreFactory()
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = currentUser,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayLocal).assertExists()
    rule.onNodeWithText(strings.prepareGamePlayOnline).assertExists()
  }

  @Test
  fun given_playScreen_when_clickingNewGameAndLocalPlay_then_localGameCallbackIsCalled() = runTest {
    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("userId2", ProfileDocument(name = "user2"))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = currentUser,
                onGameItemClick = {},
                navigateToPrepareGame = {},
                navigateToLocalGame = {
                  channel.trySend(Unit)
                  channel.close()
                },
            )
          }
        }

    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayLocal).performClick()

    Truth.assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_playScreen_when_clickingNewGameAndOnlinePlay_then_onlineGameCallbackIsCalled() =
      runTest {
    val auth = emptyAuth()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("userId2", ProfileDocument(name = "user2"))
      }
    }
    val assets = emptyAssets()

    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPlayScreen(
                user = currentUser,
                onGameItemClick = {},
                navigateToPrepareGame = {
                  channel.trySend(Unit)
                  channel.close()
                },
                navigateToLocalGame = {},
            )
          }
        }

    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayOnline).performClick()

    Truth.assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }
}
