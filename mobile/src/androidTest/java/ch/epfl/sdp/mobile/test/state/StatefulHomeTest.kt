package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulHomeTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultSection_isSocial() = runTest {
    val api = AuthenticationFacade(emptyAuth(), emptyStore())
    api.signUpWithEmail("email", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val mockSocialFacade = mockk<SocialFacade>()
    every { mockSocialFacade.search("") } returns emptyFlow()
    val mockAuthenticationFacade = mockk<AuthenticationFacade>()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(mockAuthenticationFacade, mockSocialFacade) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickingSettingsTab_selectsSettingsSection() = runTest {
    val api = AuthenticationFacade(emptyAuth(), emptyStore())
    api.signUpWithEmail("email", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val mockSocialFacade = mockk<SocialFacade>()
    every { mockSocialFacade.search("") } returns emptyFlow()
    val mockAuthenticationFacade = mockk<AuthenticationFacade>()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(mockAuthenticationFacade, mockSocialFacade) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(isSelected())
  }

  @Test
  fun clickSocialSection_selectsSocialSection() = runTest {
    val api = AuthenticationFacade(emptyAuth(), emptyStore())
    api.signUpWithEmail("email", "name", "password")
    val user = api.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val mockSocialFacade = mockk<SocialFacade>()
    every { mockSocialFacade.search("") } returns emptyFlow()
    val mockAuthenticationFacade = mockk<AuthenticationFacade>()
    val strings =
        rule.setContentWithLocalizedStrings {
          ProvideFacades(mockAuthenticationFacade, mockSocialFacade) { StatefulHome(user) }
        }
    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }
}
