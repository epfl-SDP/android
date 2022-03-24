package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulFollowingScreen
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

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
                }))

    val mockSocialFacade = mockk<SocialFacade>()
    val mockAuthenticationFacade = mockk<AuthenticationFacade>()
    val mockNavHostController = mockk<NavHostController>()

    every { mockSocialFacade.search("") } returns emptyFlow()

    rule.setContent {
      ProvideFacades(mockAuthenticationFacade, mockSocialFacade) {
        StatefulFollowingScreen(mockUser, mockNavHostController)
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
      val mockNavHostController = mockk<NavHostController>()

      authenticationFacade.signUpWithEmail("example", "name", "password")
      val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
      val strings =
          rule.setContentWithLocalizedStrings {
            ProvideFacades(authenticationFacade, socialFacade) {
              StatefulFollowingScreen(user, mockNavHostController)
            }
          }
      rule.onNodeWithText(strings.socialSearchBarPlaceHolder).performTextInput(name)
      rule.onNodeWithText(strings.socialFollow).performClick()
      val profile =
          store
              .collection("users")
              .document("other")
              .asFlow<ProfileDocument>()
              .filterNotNull()
              .first()
      Truth.assertThat(profile.followers).contains(user.uid)
    }
  }
}
