package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.StatefulHome
import ch.epfl.sdp.mobile.test.application.AlwaysSucceedingAuthenticationFacade
import org.junit.Rule
import org.junit.Test

class StatefulHomeTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun defaultSection_isSocial() {
    val api = AlwaysSucceedingAuthenticationFacade()
    val user = api.AuthenticatedUser("email")
    val strings = rule.setContentWithLocalizedStrings { StatefulHome(user) }
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }

  @Test
  fun clickingSettingsTab_selectsSettingsSection() {
    val api = AlwaysSucceedingAuthenticationFacade()
    val user = api.AuthenticatedUser("email")
    val strings = rule.setContentWithLocalizedStrings { StatefulHome(user) }
    rule.onNodeWithText(strings.sectionSettings).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsNotSelected()
    rule.onAllNodesWithText(strings.sectionSettings).assertAny(isSelected())
  }

  @Test
  fun clickSocialSection_selectsSocialSection() {
    val api = AlwaysSucceedingAuthenticationFacade()
    val user = api.AuthenticatedUser("email")
    val strings = rule.setContentWithLocalizedStrings { StatefulHome(user) }
    rule.onNodeWithText(strings.sectionSocial).performClick()
    rule.onNodeWithText(strings.sectionSocial).assertIsSelected()
    rule.onNodeWithText(strings.sectionSettings).assertIsNotSelected()
  }
}
