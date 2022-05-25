package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.state.StatefulSettingsScreen
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AuthenticatedUserProfileScreenStateTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun correctBehaviour_takesTheUsernameCorrectly() = runTest {
    val env = rule.setContentWithTestEnvironment { StatefulSettingsScreen(user, {}, {}, {}, {}) }
    rule.onNodeWithText(env.user.name).assertExists()
  }
}
