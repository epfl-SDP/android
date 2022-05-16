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
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.FakeAssetManager
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.UnknownCommandSpeechRecognizerFactory
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
    val assets = emptyAssets()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    api.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess, speech, tournament) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickingSettingsTab_selectsSettingsSection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets = emptyAssets()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    api.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess, speech, tournament) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(isSelected())
  }

  @Test
  fun clickSocialSection_selectsSocialSection() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets = emptyAssets()
    val api = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    api.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(api, social, chess, speech, tournament) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickPlaySection_selectsPlaySection() = runTest {
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
          ProvideFacades(facade, social, chess, speech, tournament) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionPlay).performClick()
    rule.onNodeWithText(strings.sectionPlay).assertIsSelected()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
  }

  @Test
  fun given_statefulHome_when_clickingOnContestsSection_then_contestsScreenDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
        }

    rule.onNodeWithText(strings.sectionContests).assertExists()
    rule.onNodeWithText(strings.sectionContests).performClick()
    rule.onAllNodesWithText(strings.sectionContests).assertCountEquals(2)
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
    val assets = emptyAssets()
    val facade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    facade.signInWithEmail("email@example.org", "password")
    val user = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(facade, social, chess, speech, tournament) { StatefulHome(user) }
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
    val assets = emptyAssets()
    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("email@epfl.ch", "name", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament) {
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
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
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
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
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
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
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
  fun given_statefulHome_when_creatingOnlineGame_then_showsGameWithOpponent() = runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = buildStore {
      collection("users") { document("userId2", ProfileDocument(name = "user2")) }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val user2 =
        social.profile(uid = "userId2", user = currentUser).filterIsInstance<Profile>().first()
    currentUser.follow(user2)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
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
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
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
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament) {
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
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade = SpeechFacade(UnknownCommandSpeechRecognizerFactory)
    val tournamentFacade = TournamentFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
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
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade = SpeechFacade(UnknownCommandSpeechRecognizerFactory)
    val tournamentFacade = TournamentFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val player2 = socialFacade.profile(uid = "2", user = user).filterIsInstance<Profile>().first()
    user.follow(player2)

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
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
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament) {
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
  fun given_visitedProfileScreen_when_cancelButtonClicked_then_socialScreenIsDisplayed() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", name = "A"))
        document("2", ProfileDocument("2", name = "B"))
      }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)
    val assets = emptyAssets()
    val chessFacade = ChessFacade(auth, store, assets)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val follower = socialFacade.profile("2").first()!!
    user.follow(follower)

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament) {
            StatefulHome(user)
          }
        }

    rule.onNodeWithText("B").assertExists().performClick()
    rule.onNodeWithContentDescription("cancel").assertExists().performClick()
    rule.onNodeWithText(strings.socialFollowingTitle).assertExists()
  }
  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_nameShouldBeUpdated() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument(name = "test", emoji = ":)")) }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament) { Navigation() }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNode(hasText("test") and hasSetTextAction()).performTextReplacement("test2")
    rule.onNodeWithText(strings.settingEditSave).performClick()
    rule.onNodeWithText("test2").assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_cancelWithoutSave() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") { document("1", ProfileDocument("1", name = "test", emoji = ":)")) }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament) { Navigation() }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(strings.profileEditNameIcon).performClick()
    rule.onNodeWithText(strings.settingEditCancel).performClick()
    rule.onNodeWithText("test").assertIsDisplayed()
  }

  @Test
  fun given_puzzleSelectionScreen_when_puzzleClicked_then_correspondingPuzzleOpened() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "00008,r6k/pp2r2p/4Rp1Q/3p4/8/1N1P2R1/PqP2bPP/7K b - - 0 24,f2g3 e6e7 b2b1 b3c1 b1c1 h6c1,1852,74,97,1444,crushing hangingPiece long middlegame,https://lichess.org/787zsVup/black#48\n" +
                    "0000D,5rk1/1p3ppp/pq3b2/8/8/1P1Q1N2/P4PPP/3R2K1 w - - 2 27,d3d6 f8d8 d6d8 f6d8,1580,73,97,11995,advantage endgame short,https://lichess.org/F8M8OS71#53",
        )

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade = SpeechFacade(UnknownCommandSpeechRecognizerFactory)
    val tournamentFacade = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("email@example.org", "user", "password")

    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionPuzzles).performClick()
    rule.onNodeWithText("00008", substring = true).performClick()
    rule.onNodeWithText(strings.puzzleNumber("00008")).assertExists()
    rule.onNodeWithText(strings.puzzleRating("1852")).assertExists()
  }

  @Test
  fun given_home_when_routeUpdatedToTournamentDetails_then_displaysTournament() = runTest {
    val env =
        rule.setContentWithTestEnvironment {
          val controller = rememberNavController()
          StatefulHome(
              user = user,
              // We must call controller.navigate() after the first composition (so the
              // NavController has all the routes set up), and a Modifier which has the property
              // of being called after composition is onGloballyPositioned.
              modifier = Modifier.onGloballyPositioned { controller.navigate("tournament/123") },
              controller = controller,
          )
        }
    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document("123")
        .set(TournamentDocument(name = "Hello"))
    rule.onNodeWithText("Hello", ignoreCase = true).assertIsDisplayed()
  }

  @Test
  fun given_home_when_creatingTournament_then_navigatesToTournament() = runTest {
    val env = rule.setContentWithTestEnvironment { StatefulHome(user = user) }
    rule.onNodeWithText(env.strings.sectionContests).performClick()
    rule.onNodeWithText(env.strings.newContest).performClick()
    rule.onNodeWithText(env.strings.tournamentsCreateNameHint).performTextInput("Hello")
    rule.onNodeWithText("1").performClick() // Best of 1
    rule.onNodeWithText(env.strings.tournamentsCreateMaximumPlayerHint).performTextInput("2")
    rule.onNodeWithText(env.strings.tournamentsCreateQualifierSize0).performClick()
    rule.onNodeWithText(env.strings.tournamentsCreateElimDepthFinal).performClick()
    rule.onNodeWithText(env.strings.tournamentsCreateActionCreate).assertIsEnabled().performClick()

    // We can now join the tournament, since we've navigated to its details screen.
    rule.onNodeWithText(env.strings.tournamentsBadgeJoin).assertIsDisplayed()
    rule.onNodeWithText("Hello", ignoreCase = true).assertIsDisplayed()
  }

  @Test
  fun given_tournamentScreen_when_clickingCreate_createTournamentDialogIsOpened() = runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
        }

    rule.onNodeWithText(strings.sectionContests).performClick()
    rule.onNodeWithText(strings.newContest).performClick()

    rule.onNodeWithText(strings.tournamentsCreateTitle).assertExists()
  }

  @Test
  fun given_tournamentScreen_when_clickingCancel_createTournamentDialogIsClosed() = runTest {
    val auth = emptyAuth()
    val assets = emptyAssets()
    val store = emptyStore()

    val authFacade = AuthenticationFacade(auth, store)
    val social = SocialFacade(auth, store)
    val chess = ChessFacade(auth, store, assets)
    val speech = SpeechFacade(FailingSpeechRecognizerFactory)
    val tournament = TournamentFacade(auth, store)

    authFacade.signUpWithEmail("user1@email", "user1", "password")
    val currentUser = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(authFacade, social, chess, speech, tournament) {
            StatefulHome(currentUser)
          }
        }

    rule.onNodeWithText(strings.sectionContests).performClick()
    rule.onNodeWithText(strings.newContest).performClick()
    rule.onNodeWithText(strings.tournamentsCreateTitle).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateActionCancel).performClick()
    rule.onNodeWithText(strings.tournamentsCreateTitle).assertDoesNotExist()
  }

  @Test
  fun given_contestScreen_when_tournamentIsClicked_then_itsTournamentDetailsScreenIsDisplayed() =
      runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection(TournamentDocument.Collection) {
        document("id1", TournamentDocument("tid1", "1", "Tournament 1"))
      }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade = SpeechFacade(UnknownCommandSpeechRecognizerFactory)
    val tournamentFacade = TournamentFacade(auth, store)

    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionContests).performClick()
    rule.onNodeWithText("Tournament 1").assertExists()
    rule.onNodeWithText("Tournament 1").performClick()
    rule.onNodeWithText(strings.tournamentsBadgeJoin).assertIsDisplayed()
    rule.onNodeWithText("Tournament 1", ignoreCase = true).assertIsDisplayed()
  }
}
