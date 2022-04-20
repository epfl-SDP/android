package ch.epfl.sdp.mobile.test.state

import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulFollowingScreen
import com.google.common.truth.Truth.*
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
                }))

    val mockSocialFacade = mockk<SocialFacade>()
    val mockAuthenticationFacade = mockk<AuthenticationFacade>()
    val mockChessFacade = mockk<ChessFacade>()

    every { mockSocialFacade.search("", mockUser) } returns emptyFlow()

    rule.setContent {
      ProvideFacades(mockAuthenticationFacade, mockSocialFacade, mockChessFacade) {
        StatefulFollowingScreen(mockUser, {})
      }
    }
    rule.onNodeWithText("Hans Peter").assertExists()
  }

  @Test
  fun searchList_onFollowClickUserIsFollowed() {
    runTest {
      val name = "Fred"
      val auth = emptyAuth()
      val store = buildStore {
        collection("users") { document("other", ProfileDocument(name = name)) }
      }
      val authenticationFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store)

      authenticationFacade.signUpWithEmail("example@epfl.ch", "name", "password")
      val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
              StatefulFollowingScreen(user, {})
            }
          }
      rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput(name)
      rule.onNodeWithText(strings.socialPerformFollow).performClick()
      val profile =
          store
              .collection("users")
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
      val auth = emptyAuth()
      val store = buildStore {
        collection("users") { document("other", ProfileDocument(name = name)) }
      }
      val authenticationFacade = AuthenticationFacade(auth, store)
      val socialFacade = SocialFacade(auth, store)
      val chessFacade = ChessFacade(auth, store)

      authenticationFacade.signUpWithEmail("example@epfl.ch", "name", "password")
      val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authenticationFacade, socialFacade, chessFacade) {
              StatefulFollowingScreen(user, {})
            }
          }
      rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput(name)
      rule.onNodeWithText(strings.socialPerformFollow).performClick()
      rule.onNodeWithText(strings.socialPerformUnfollow).assertExists()
      rule.onNodeWithText(strings.socialPerformUnfollow).performClick()
      rule.onNodeWithText(strings.socialPerformFollow).assertExists()
    }
  }

  @Test
  fun focusedSearchField_isInSearchMode() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val user =
        with(AuthenticationFacade(auth, store)) {
          signUpWithEmail("email@epfl.ch", "name", "password")
          currentUser.filterIsInstance<AuthenticatedUser>().first()
        }

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(
              authentication = remember { AuthenticationFacade(auth, store) },
              social = remember { SocialFacade(auth, store) },
              chess = remember { ChessFacade(auth, store) },
          ) { StatefulFollowingScreen(user, onShowProfileClick = {}) }
        }

    rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performClick()
    rule.onNodeWithText(strings.socialSearchEmptyTitle).assertIsDisplayed()
    rule.onNodeWithText(strings.socialSearchEmptySubtitle).assertIsDisplayed()
  }

  @Test
  fun unfocusedSearchField_withText_isInSearchMode() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val user =
        with(AuthenticationFacade(auth, store)) {
          signUpWithEmail("email@epfl.ch", "name", "password")
          currentUser.filterIsInstance<AuthenticatedUser>().first()
        }

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(
              authentication = remember { AuthenticationFacade(auth, store) },
              social = remember { SocialFacade(auth, store) },
              chess = remember { ChessFacade(auth, store) },
          ) { StatefulFollowingScreen(user, onShowProfileClick = {}) }
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
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("a", ProfileDocument(name = "Alexandre")) }
    }
    val user =
        with(AuthenticationFacade(auth, store)) {
          signUpWithEmail("email@epfl.ch", "name", "password")
          currentUser.filterIsInstance<AuthenticatedUser>().first()
        }

    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(
              authentication = remember { AuthenticationFacade(auth, store) },
              social = remember { SocialFacade(auth, store) },
              chess = remember { ChessFacade(auth, store) },
          ) { StatefulFollowingScreen(user, onShowProfileClick = {}) }
        }

    rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput("Alex")
    rule.onNodeWithText("Alexandre").assertIsDisplayed()
  }
}
