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
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.state.Navigation
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.emptyAssets
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.onePuzzleAssets
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.twoPuzzleAssets
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.sound.fake.FakeSoundPlayer
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.UnknownCommandSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.time.fake.FakeTimeProvider
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import ch.epfl.sdp.mobile.test.ui.home.FollowingSectionRobot
import ch.epfl.sdp.mobile.test.ui.home.HomeSectionRobot
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
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }
    FollowingSectionRobot(rule, strings).assertIsDisplayed()
  }

  @Test
  fun clickingSettingsTab_selectsSettingsSection() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }
    HomeSectionRobot(rule, strings).clickSettingsTab().assertIsDisplayed()
  }

  @Test
  fun clickSocialSection_selectsSocialSection() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }
    HomeSectionRobot(rule, strings).clickFollowingTab().assertIsDisplayed()
  }

  @Test
  fun clickPlaySection_selectsPlaySection() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }
    HomeSectionRobot(rule, strings).clickPlayTab().assertIsDisplayed()
  }

  @Test
  fun given_statefulHome_when_clickingOnContestsSection_then_contestsScreenDisplayed() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }
    HomeSectionRobot(rule, strings).clickTournamentsTab().assertIsDisplayed()
  }

  @Test
  fun clickOnPlayer_inFollowerScreen_openProfileScreen() = runTest {
    val (_, infra, strings, user) =
        rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

    infra
        .store
        .collection(ProfileDocument.Collection)
        .document()
        .set(ProfileDocument(name = "testName", followers = listOf(user.uid)))

    FollowingSectionRobot(rule, strings).clickProfile(name = "testName") {
      assertIsDisplayed()
      assertHasName("testName")
    }
  }

  @Test
  fun gameRoute_displaysAChessGame() = runTest {
    val (_, _, strings) =
        rule.setContentWithAuthenticatedTestEnvironment {
          val controller = rememberNavController()
          StatefulHome(
              user = user,
              // We must call controller.navigate() after the first composition (so the
              // NavController has all the routes set up), and a Modifier which has the property
              // of being called after composition is onGloballyPositioned.
              modifier = Modifier.onGloballyPositioned { controller.navigate("match/123") },
              controller = controller,
          )
        }

    rule.onNodeWithContentDescription(strings.boardContentDescription).assertExists()
  }

  @Test
  fun creatingGameFromPrepareGameScreen_opensGameScreen() = runTest {
    val store = buildStore {
      collection(ProfileDocument.Collection) { document("id", ProfileDocument(name = "user2")) }
    }
    val (facades, _, strings, user) =
        rule.setContentWithAuthenticatedTestEnvironment(store = store) { StatefulHome(user) }

    user.follow(facades.social.profile("id", user).first() ?: throw IllegalStateException())

    HomeSectionRobot(rule, strings)
        .clickPlayTab()
        .clickNewOnlineGame {
          assertIsDisplayed()
          selectOpponent("user2")
        }
        .clickPlay()
        .assertIsDisplayed()
  }

  @Test
  fun clickingOnPlayButtonFromPrepareGameScreen_withNoOpponentSelected_doesNothing() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

    val prepareOnlineGame = HomeSectionRobot(rule, strings).clickPlayTab().clickNewOnlineGame()

    prepareOnlineGame.clickPlay { assertIsNotDisplayed() }
    prepareOnlineGame.assertIsDisplayed()
  }

  @Test
  fun cancelingPrepareGameScreen_returnsToPlaySection() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

    val playSection = HomeSectionRobot(rule, strings).clickPlayTab()

    playSection.clickNewOnlineGame {
      assertIsDisplayed()
      clickCancel()
    }

    playSection.assertIsDisplayed()
  }

  @Test
  fun given_statefulHome_when_creatingOnlineGame_then_showsGameWithOpponent() = runTest {
    val store = buildStore {
      collection(ProfileDocument.Collection) { document("id", ProfileDocument(name = "user2")) }
    }
    val (facade, _, strings, user) =
        rule.setContentWithAuthenticatedTestEnvironment(store = store) { StatefulHome(user) }

    user.follow(facade.social.profile("id").first() ?: throw IllegalStateException())

    HomeSectionRobot(rule, strings)
        .clickPlayTab()
        .clickNewOnlineGame { selectOpponent("user2") }
        .clickPlay {
          assertIsDisplayed()
          assertHasPlayer("user2")
        }
  }

  @Test
  fun given_statefulHome_when_creatingLocalGameFromUI_then_gameScreenOpens() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

    HomeSectionRobot(rule, strings).clickPlayTab().clickNewLocalGame().assertIsDisplayed()
  }

  @Test
  fun clickOnGame_inStatefulPlayScreen_openGame() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "test2"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection(ChessDocument.Collection) {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament, settings) {
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
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "Player 1"))
        document("2", ProfileDocument("2", name = "Player 2"))
      }
      collection(ChessDocument.Collection) {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(
              authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
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
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "Player 1"))
        document("2", ProfileDocument("2", name = "Player 2"))
      }
      collection(ChessDocument.Collection) {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val player2 = socialFacade.profile(uid = "2", user = user).filterIsInstance<Profile>().first()
    user.follow(player2)

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(
              authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
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

  // @Test
  fun given_aOnGoingGame_when_clickOnArButton_then_displayArScreen() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "test2"))
        document("2", ProfileDocument("2", name = "test"))
      }
      collection(ChessDocument.Collection) {
        document(
            "id", ChessDocument(uid = "786", whiteId = "1", blackId = "2", moves = listOf("e2-e4")))
      }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speech =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournament = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(authFacade, socialFacade, chessFacade, speech, tournament, settings) {
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
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "A"))
        document("2", ProfileDocument("2", name = "B"))
      }
    }
    val authFacade = AuthenticationFacade(auth, store)

    authFacade.signInWithEmail("email@example.org", "password")

    val env =
        rule.setContentWithAuthenticatedTestEnvironment(auth = auth, store = store) {
          StatefulHome(user)
        }

    val user = env.facades.auth.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val follower = env.facades.social.profile("2").first()!!
    user.follow(follower)

    rule.onNodeWithText("B").assertExists().performClick()
    rule.onNodeWithContentDescription(env.strings.socialCloseVisitedProfile)
        .assertExists()
        .performClick()
    rule.onNodeWithText(env.strings.socialFollowingTitle).assertExists()
  }
  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_nameShouldBeUpdated() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument(name = "test", emoji = ":)"))
      }
    }
    val authFacade = AuthenticationFacade(auth, store)
    authFacade.signInWithEmail("email@example.org", "password")

    val env = rule.setContentWithTestEnvironment(auth = auth, store = store) { Navigation() }

    rule.onNodeWithText(env.strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(env.strings.profileEditNameIcon).performClick()
    rule.onNode(hasText("test") and hasSetTextAction()).performTextReplacement("test2")
    rule.onNodeWithText(env.strings.settingEditSave).performClick()
    rule.onNodeWithText("test2").assertIsDisplayed()
  }

  @Test
  fun given_userIsLoggedIn_when_editProfileName_then_cancelWithoutSave() = runTest {
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "test", emoji = ":)"))
      }
    }

    val authFacade = AuthenticationFacade(auth, store)
    authFacade.signInWithEmail("email@example.org", "password")

    val env = rule.setContentWithTestEnvironment(auth = auth, store = store) { Navigation() }

    rule.onNodeWithText(env.strings.sectionSettings).performClick()
    rule.onNodeWithContentDescription(env.strings.profileEditNameIcon).performClick()
    rule.onNodeWithText(env.strings.settingEditCancel).performClick()
    rule.onNodeWithText("test").assertIsDisplayed()
  }

  @Test
  fun given_puzzleSelectionScreen_when_puzzleClicked_then_correspondingPuzzleOpened() = runTest {
    val (assets, puzzleIds) = twoPuzzleAssets()
    val env =
        rule.setContentWithAuthenticatedTestEnvironment(assets = assets) {
          StatefulHome(user = user)
        }

    rule.onNodeWithText(env.strings.sectionPuzzles).performClick()
    rule.onNodeWithText(puzzleIds[0], substring = true).performClick()
    rule.onNodeWithText(env.strings.puzzleNumber(puzzleIds[0])).assertExists()
    rule.onNodeWithText(env.strings.puzzleRating("1852")).assertExists()
  }

  @Test
  fun given_home_when_routeUpdatedToTournamentDetails_then_displaysTournament() = runTest {
    val env =
        rule.setContentWithAuthenticatedTestEnvironment {
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
    val env = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user = user) }
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
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

    rule.onNodeWithText(strings.sectionContests).performClick()
    rule.onNodeWithText(strings.newContest).performClick()

    rule.onNodeWithText(strings.tournamentsCreateTitle).assertExists()
  }

  @Test
  fun given_tournamentScreen_when_clickingCancel_createTournamentDialogIsClosed() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

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
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(TournamentDocument.Collection) {
        document("id1", TournamentDocument("tid1", "1", "Tournament 1"))
      }
    }
    val assets = emptyAssets()

    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")
    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(
              authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
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

  @Test
  fun given_settingsScreen_when_clickingOnPuzzle_then_ItIsOpened() = runTest {
    val id = "1"
    val auth = buildAuth { user("email@example.org", "password", id) }
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document(id, ProfileDocument(solvedPuzzles = listOf("00008")))
      }
    }

    val (assets, puzzleIds) = onePuzzleAssets()
    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")

    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(
              authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.profilePuzzle).performClick()
    rule.onNodeWithText(puzzleIds.first(), substring = true).performClick()
    rule.onNodeWithText(strings.puzzleNumber(puzzleIds.first())).assertExists()
  }

  @Test
  fun given_profileScreen_when_clickingOnPuzzle_then_itIsOpened() = runTest {
    val id = "1"
    val auth = buildAuth { user("email@example.org", "password", id) }
    val (assets, puzzleIds) = onePuzzleAssets()
    val dataStoreFactory = emptyDataStoreFactory()
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document(
            id,
            ProfileDocument(
                name = "Username1", followers = listOf(id), solvedPuzzles = listOf(puzzleIds[0])))
      }
    }
    val authFacade = AuthenticationFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val socialFacade = SocialFacade(auth, store)
    val speechFacade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val tournamentFacade = TournamentFacade(auth, dataStoreFactory, store, FakeTimeProvider)
    val settings = SettingsFacade(dataStoreFactory)

    authFacade.signInWithEmail("email@example.org", "password")

    val user = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    val strings =
        rule.setContentWithLocalizedStrings {
          val controller = rememberNavController()
          ProvideFacades(
              authFacade, socialFacade, chessFacade, speechFacade, tournamentFacade, settings) {
            StatefulHome(
                user = user,
                controller = controller,
            )
          }
        }

    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText("Username1").performClick()
    rule.onNodeWithText(strings.profilePuzzle).performClick()
    rule.onNodeWithText(puzzleIds[0], substring = true).performClick()
    rule.onNodeWithText(strings.puzzleNumber(puzzleIds[0])).assertExists()
  }

  @Test
  fun given_home_when_navigatingToFiltersAndBack_then_goesBack() = runTest {
    val (_, _, strings) = rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }
    with(strings) {
      rule.onNodeWithText(sectionContests).performClick()
      rule.onNodeWithContentDescription(tournamentsFilter).performClick()
      rule.onNodeWithText(tournamentsFilterTitle).assertIsDisplayed() // On the filters dialog
      rule.onNodeWithContentDescription(tournamentDetailsBackContentDescription).performClick()
      rule.onNodeWithText(tournamentsFilterTitle).assertDoesNotExist() // On the contest list
    }
  }

  @Test
  fun given_social_when_clickOnPlay_then_should_startAGame() = runTest {
    val (_, infra, strings, user) =
        rule.setContentWithAuthenticatedTestEnvironment { StatefulHome(user) }

    val opponentName = "B"
    infra
        .store
        .collection(ProfileDocument.Collection)
        .document()
        .set(ProfileDocument(name = opponentName, followers = listOf(user.uid)))

    with(strings) {
      rule.onNodeWithText(sectionSocial).performClick()
      rule.onNodeWithText(socialPerformPlay).performClick()
      rule.onNodeWithText(prepareGamePlay).performClick()
      rule.onNodeWithText(opponentName).assertExists()
    }
  }
}
