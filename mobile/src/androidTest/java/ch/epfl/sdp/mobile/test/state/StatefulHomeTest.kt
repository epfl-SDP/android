package ch.epfl.sdp.mobile.test.state

import android.Manifest
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.rule.GrantPermissionRule
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuccessfulSpeechRecognizerFactory
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulHomeTest {

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultSection_isSocial() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    api.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess, speech) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickingSettingsTab_selectsSettingsSection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    api.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess, speech) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(isSelected())
  }

  @Test
  fun clickSocialSection_selectsSocialSection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    api.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess, speech) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickPlaySection_selectsPlaySection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    facade.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.sectionPlay).assertIsSelected()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
  }

  @Test
  fun given_statefulHome_when_clickingOnContestsSection_then_contestsScreenDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech) { StatefulHome(currentUser) }
        }

    rule.onNodeWithText(strings.sectionContests).assertExists()
    rule.onNodeWithText(strings.sectionContests).performClick()
    rule.onNodeWithText(strings.sectionContests).assertIsDisplayed()
    rule.onNodeWithText(strings.sectionContests).assertIsSelected()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
  }

  @Test
  fun clickOnPlayer_inFollowerScreen_openProfileScreen() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument())
        document("2", ProfileDocument(emoji = ":)", name = "testName", followers = listOf("1")))
      }
    }
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    facade.signInWithEmail("email@example.org", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText("testName").assertExists()
    rule.onNodeWithText("testName").performClick()
    rule.onNodeWithText(strings.profilePastGames).assertExists()
  }

  @Test
  fun gameRoute_displaysAChessGame() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech) {
            StatefulHome(
                user = user,
                // We must call controller.navigate() after the first composition (so the
                // NavController has all the routes set up), and a Modifier which has the property
                // of being called after composition is onGloballyPositioned.
                modifier = Modifier.onGloballyPositioned { controller.navigate("match/123") },
                controller = controller,
            )
          }
        }

    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
  }

  @Test
  fun creatingGameFromPrepareGameScreen_opensGameScreen() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech) { StatefulHome(currentUser) }
        }

    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayOnline).performClick()
    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()
    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
  }

  @Test
  fun clickingOnPlayButtonFromPrepareGameScreen_withNoOpponentSelected_doesNothing() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech) { StatefulHome(currentUser) }
        }

    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayOnline).performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()

    rule.onNodeWithContentDescription(strings.boardContentDescription).assertDoesNotExist()
    rule.onNodeWithText("user2").assertExists()
  }

  @Test
  fun cancelingPreparegameScreen_returnsToPlaySection() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech) { StatefulHome(currentUser) }
        }

    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayOnline).performClick()

    rule.onNodeWithText("user2").assertExists()
    rule.onNodeWithText(strings.prepareGameCancel).performClick()
    rule.onNodeWithText("user2").assertDoesNotExist()
    rule.onNodeWithText(strings.newGame).assertExists()
  }

  @Test
  fun given_statefulHome_when_creatingOnlineGameFromUI_then_gameScreenOpensWithCorrectOpponent() =
      runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech) { StatefulHome(currentUser) }
        }

    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayOnline).performClick()
    rule.onNodeWithText("user2").performClick()
    rule.onNodeWithText(strings.prepareGamePlay).performClick()

    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
    rule.onNodeWithText("user2").assertExists()
  }

  @Test
  fun given_statefulHome_when_creatingLocalGameFromUI_then_gameScreenOpens() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech) { StatefulHome(currentUser) }
        }

    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.newGame).performClick()
    rule.onNodeWithText(strings.prepareGamePlayLocal).performClick()

    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
  }

  @Test
  fun clickOnGame_inStatefulPlayScreen_openGame() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", name = "test2"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection("games") {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.profileMatchTitle("test")).assertExists()
    rule.onNodeWithText(strings.profileMatchTitle("test")).performClick()
    rule.onNodeWithText("test2").assertExists()
    rule.onNodeWithText("test").assertExists()
  }

  @Test
  fun given_settingScreen_when_profileMatchIsClickedOn_then_MatchIsDisplayed() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", name = "Player 1"))
        document("2", ProfileDocument("2", name = "Player 2"))
      }
      collection("games") {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade = SpeechFacade(SuccessfulSpeechRecognizerFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.profileMatchTitle("Player 2")).assertExists()
    rule.onNodeWithText(strings.profileMatchTitle("Player 2")).performClick()
    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
    rule.onNodeWithText("Player 1").assertExists()
    rule.onNodeWithText("Player 2").assertExists()
  }

  @Test
  fun given_visitedProfileScreen_when_profileMatchIsClickedOn_then_MatchIsDisplayed() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", name = "Player 1"))
        document("2", ProfileDocument("2", name = "Player 2"))
      }
      collection("games") {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade = SpeechFacade(SuccessfulSpeechRecognizerFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val player2 = socialFacade.profile(uid = "2", user = user).filterIsInstance<Profile>().first()
    user.follow(player2)

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText("Player 2").assertExists()
    rule.onNodeWithText("Player 2").performClick()
    rule.onNodeWithText(strings.profileMatchTitle("Player 1")).performClick()
    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
    rule.onNodeWithText("Player 1").assertExists()
    rule.onNodeWithText("Player 2").assertExists()
  }

  @Test
  fun given_aOnGoingGame_when_clickOnArButton_then_displayArScreen() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", name = "test2"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection("games") {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }
    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.profileMatchTitle("test")).assertExists().performClick()
    rule.onNodeWithContentDescription(strings.gameShowAr).assertExists().performClick()
    withCanceledIntents {
      rule.onNodeWithContentDescription(strings.arContentDescription).assertExists()
    }
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_nameShouldBeUpdated() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument(name = "test", emoji = ":)")) }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signInWithEmail("email@example.org", "password")

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade, speech) { Navigation() }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNode(hasText("test") and hasSetTextAction()).performTextInput("2")
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText("test2").assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_cancelWithoutSave() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument("1", name = "test", emoji = ":)")) }
    }

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signInWithEmail("email@example.org", "password")

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade, speech) { Navigation() }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNodeWithText(strings.settingEditCancel).performClick()
    rule.onNodeWithText("test").assertIsDisplayed()
  }
}
