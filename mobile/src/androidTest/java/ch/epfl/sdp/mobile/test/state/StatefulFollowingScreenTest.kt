package ch.epfl.sdp.mobile.test.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.PuzzleId
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.settings.SettingsFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.tournaments.TournamentFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulFollowingScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.sound.fake.FakeSoundPlayer
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

private class FakeProfile(
    override val uid: String,
) : Profile {
  override val backgroundColor: Profile.Color = Profile.Color.Default
  override val name: String = "Andy"
  override val emoji: String = ":3"
  override val followed: Boolean = false
  override val solvedPuzzles = emptyList<PuzzleId>()
}

class StatefulFollowingScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultMode_displayCorrectFollowers() {
    val mockUser = mockk<AuthenticatedUser>()
    every { mockUser.following } returns
        flowOf(
            listOf<Profile>(
                object : Profile {
                  override val emoji: String
                    get() = ":>"
                  override val name: String
                    get() = "Hans Peter"
                  override val backgroundColor: Color
                    get() = Color.Default
                  override val uid: String
                    get() = ""
                  override val followed: Boolean
                    get() = false
                  override val solvedPuzzles: List<PuzzleId>
                    get() = emptyList()
                }))

    val mockSocialFacade = mockk<SocialFacade>()
    val mockAuthenticationFacade = mockk<AuthenticationFacade>()
    val mockChessFacade = mockk<ChessFacade>()
    val mockSpeechFacade =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    val mockTournamentFacade = mockk<TournamentFacade>()
    val mockSettingsFacade = mockk<SettingsFacade>()

    every { mockSocialFacade.search("", mockUser) } returns emptyFlow()

    rule.setContent {
      ProvideFacades(
          mockAuthenticationFacade,
          mockSocialFacade,
          mockChessFacade,
          mockSpeechFacade,
          mockTournamentFacade,
          mockSettingsFacade) { StatefulFollowingScreen(mockUser, {}, {}) }
    }
    rule.onNodeWithText("Hans Peter").assertExists()
  }

  @Test
  fun searchList_onFollowClickUserIsFollowed() {
    runTest {
      val name = "Fred"

      val (_, infra, strings, user) =
          rule.setContentWithAuthenticatedTestEnvironment { StatefulFollowingScreen(user, {}, {}) }

      infra
          .store
          .collection(ProfileDocument.Collection)
          .document("other")
          .set(ProfileDocument(name = name))

      rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput(name)
      rule.onNodeWithText(strings.socialPerformFollow).performClick()
      val profile =
          infra
              .store
              .collection(ProfileDocument.Collection)
              .document("other")
              .asFlow<ProfileDocument>()
              .filterNotNull()
              .first()
      assertThat(profile.followers).contains(user.uid)
    }
  }

  @Test
  fun searchList_onFollowClickFollowedAppears() {
    runTest {
      val name = "Fred"
      val (_, infra, strings) =
          rule.setContentWithAuthenticatedTestEnvironment { StatefulFollowingScreen(user, {}, {}) }

      infra
          .store
          .collection(ProfileDocument.Collection)
          .document()
          .set(ProfileDocument(name = name))

      rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput(name)
      rule.onNodeWithText(strings.socialPerformFollow).performClick()
      rule.onNodeWithText(strings.socialPerformUnfollow).assertExists()
      rule.onNodeWithText(strings.socialPerformUnfollow).performClick()
      rule.onNodeWithText(strings.socialPerformFollow).assertExists()
    }
  }

  @Test
  fun focusedSearchField_isInSearchMode() = runTest {
    val (_, _, strings) =
        rule.setContentWithAuthenticatedTestEnvironment {
          StatefulFollowingScreen(user, onShowProfileClick = {}, onPlayClick = {})
        }

    rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performClick()
    rule.onNodeWithText(strings.socialSearchEmptyTitle).assertIsDisplayed()
    rule.onNodeWithText(strings.socialSearchEmptySubtitle).assertIsDisplayed()
  }

  @Test
  fun unfocusedSearchField_withText_isInSearchMode() = runTest {
    val (_, _, strings) =
        rule.setContentWithAuthenticatedTestEnvironment {
          StatefulFollowingScreen(user, onShowProfileClick = {}, onPlayClick = {})
        }

    rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput("Body")

    // Closes the keyboard.
    rule.onNodeWithText("Body").performImeAction()

    rule.onNodeWithText(strings.socialFollowingTitle).assertDoesNotExist()
    rule.onNodeWithText(strings.socialSearchEmptyTitle).assertDoesNotExist()
    rule.onNodeWithText(strings.socialSearchEmptySubtitle).assertDoesNotExist()
  }

  @Test
  fun searchingPlayerByNamePrefix_displaysPlayerName() = runTest {
    val (_, infra, strings) =
        rule.setContentWithAuthenticatedTestEnvironment {
          StatefulFollowingScreen(user, onShowProfileClick = {}, onPlayClick = {})
        }

    infra
        .store
        .collection(ProfileDocument.Collection)
        .document()
        .set(ProfileDocument(name = "Alexandre"))

    rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput("Alex")
    rule.onNodeWithText("Alexandre").assertIsDisplayed()
  }

  @Test
  fun given_userHasFollower_when_unfollowBySwiping_then_followerShouldNotBeListed() = runTest {
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "Rapunzel"))
        document("2", ProfileDocument("2", name = "Blaublau"))
      }
    }

    val (_, _, strings, user) =
        rule.setContentWithAuthenticatedTestEnvironment(store = store) {
          StatefulFollowingScreen(user, onShowProfileClick = {}, {})
        }

    user.follow(FakeProfile("2"))

    rule.onNodeWithText("Blaublau").performTouchInput(TouchInjectionScope::swipeRight)
    rule.onNodeWithContentDescription(strings.socialUnfollowIcon).performClick()

    rule.onNodeWithText("Blaublau").assertDoesNotExist()
  }

  @Test
  fun given_userHasFollower_when_swipeRightAndLeft_then_nothingHappens() = runTest {
    val store = buildStore {
      collection(ProfileDocument.Collection) {
        document("1", ProfileDocument("1", name = "Rapunzel"))
        document("2", ProfileDocument("2", name = "Blaublau"))
      }
    }

    val (_, infra, strings, user) =
        rule.setContentWithAuthenticatedTestEnvironment(store = store) {
          StatefulFollowingScreen(user, onShowProfileClick = {}, {})
        }

    user.follow(FakeProfile("2"))

    rule.onNodeWithText("Blaublau").performTouchInput(TouchInjectionScope::swipeRight)
    rule.onNodeWithText("Blaublau").performTouchInput(TouchInjectionScope::swipeLeft)

    rule.onNodeWithText("Blaublau").assertExists()
  }
}
