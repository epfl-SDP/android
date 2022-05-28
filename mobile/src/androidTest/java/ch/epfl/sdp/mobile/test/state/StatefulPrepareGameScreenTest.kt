package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulPrepareGameScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.test.ui.prepare_game.PrepareGameRobot
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPrepareGameScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultScreen_isDisplayed() = runTest {
    val (_, _, strings) =
        rule.setContentWithTestEnvironment {
          StatefulPrepareGameScreen(
              user = user,
              navigateToGame = {},
              cancelClick = {},
              opponentId = "",
          )
        }

    PrepareGameRobot(rule, strings).assertIsDisplayed()
  }

  @Test
  fun switchColorToBlack_works() = runTest {
    val (_, _, strings) =
        rule.setContentWithTestEnvironment {
          StatefulPrepareGameScreen(
              user = user,
              navigateToGame = {},
              cancelClick = {},
              opponentId = "",
          )
        }

    PrepareGameRobot(rule, strings).apply {
      playAsBlack()
      assertBlackSelected()
    }
  }

  @Test
  fun switchColorBackToWhite_works() = runTest {
    val (_, _, strings) =
        rule.setContentWithTestEnvironment {
          StatefulPrepareGameScreen(
              user = user,
              navigateToGame = {},
              cancelClick = {},
              opponentId = "",
          )
        }

    PrepareGameRobot(rule, strings).apply {
      playAsBlack()
      playAsWhite()
      assertWhiteSelected()
    }
  }

  @Test
  fun followedUser_isDisplayed() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val authUser1 = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = authUser1).filterIsInstance<Profile>().first()
    authUser1.follow(user2)

    rule.setContentWithLocalizedStrings {
      ProvideFacades(facade, social, chess, speech, tournament, settings) {
        StatefulPrepareGameScreen(
            user = authUser1, navigateToGame = {}, cancelClick = {}, opponentId = "")
      }
    }

    rule.onNodeWithText("user2").assertExists()
  }

  @Test
  fun clickingOnOpponentAndClickingPlayAsWhite_createsGameWithCorrectId() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPrepareGameScreen(
                user = currentUser, navigateToGame = {}, cancelClick = {}, opponentId = "")
          }
        }

    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()

    val fetchedMatch = chess.matches(currentUser).mapNotNull { it.firstOrNull() }.first()
    assertThat(fetchedMatch.white.filterNotNull().first().uid).isEqualTo(currentUser.uid)
    assertThat(fetchedMatch.black.filterNotNull().first().uid).isEqualTo(user2.uid)
  }

  @Test
  fun clickingOnOpponentAndClickingPlayAsBlack_createsGameWithCorrectId() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPrepareGameScreen(
                user = currentUser, navigateToGame = {}, cancelClick = {}, opponentId = "")
          }
        }

    rule.onNodeWithText(strings.prepareGameBlackColor).performClick()
    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()

    val fetchedMatch = chess.matches(currentUser).mapNotNull { it.firstOrNull() }.first()
    assertThat(fetchedMatch.white.filterNotNull().first().uid).isEqualTo(user2.uid)
    assertThat(fetchedMatch.black.filterNotNull().first().uid).isEqualTo(currentUser.uid)
  }

  @Test
  fun unfollowingAUser_DoesNotDisplayItAsProfileAnymore() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val authUser1 = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = authUser1).filterIsInstance<Profile>().first()
    authUser1.follow(user2)

    rule.setContentWithLocalizedStrings {
      ProvideFacades(facade, social, chess, speech, tournament, settings) {
        StatefulPrepareGameScreen(
            user = authUser1, navigateToGame = {}, cancelClick = {}, opponentId = "")
      }
    }

    rule.onNodeWithText("user2").assertExists()
    authUser1.unfollow(user2)
    rule.onNodeWithText("user2").assertDoesNotExist()
  }

  @Test
  fun creatingGameFromPrepareGame_callsCallback() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPrepareGameScreen(
                user = currentUser,
                navigateToGame = { _ ->
                  channel.trySend(Unit)
                  channel.close()
                },
                cancelClick = {},
                opponentId = "")
          }
        }

    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()

    assertThat(channel.receive()).isEqualTo(Unit)
  }

  @Test
  fun cancelingPrepareGame_callsCallback() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPrepareGameScreen(
                user = currentUser,
                navigateToGame = {},
                cancelClick = {
                  channel.trySend(Unit)
                  channel.close()
                },
                opponentId = "")
          }
        }

    rule.onNodeWithText(strings.prepareGameCancel).performClick()

    assertThat(channel.receive()).isEqualTo(Unit)
  }

  @Test
  fun given_NoOpponentSelected_When_ClickingPlay_Then_ButtonNotClicked() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPrepareGameScreen(
                user = currentUser,
                navigateToGame = {
                  channel.trySend(Unit)
                  channel.close()
                },
                cancelClick = {},
                opponentId = "")
          }
        }

    rule.onNodeWithText(strings.prepareGamePlay).performClick()
    assertThat(channel.tryReceive().getOrNull()).isNotEqualTo(Unit)
  }

  @Test
  fun given_SelectedOpponent_When_DeselectingItAndClickingPlay_Then_ButtonNotClicked() = runTest {
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
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    facade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val channel = Channel<Unit>(capacity = 1)
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament, settings) {
            StatefulPrepareGameScreen(
                user = currentUser,
                navigateToGame = {
                  channel.trySend(Unit)
                  channel.close()
                },
                cancelClick = {},
                opponentId = "")
          }
        }
    // Select opponent
    rule.onNodeWithText("user2").performClick()

    // Deselect opponent
    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()

    assertThat(channel.tryReceive().getOrNull()).isNotEqualTo(Unit)
  }
}
