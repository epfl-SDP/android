package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreen
import ch.epfl.sdp.mobile.ui.tournaments.ContestScreenState
import ch.epfl.sdp.mobile.ui.tournaments.newContestButton
import org.junit.Rule
import org.junit.Test

class NewContestButtonTest {

  @get:Rule val rule = createComposeRule()

  open class FakeContestScreenState : ContestScreenState {
    override fun onNewContestClick() = Unit
  }

  @Test
  fun given_newContestButton_when_displayed_then_showsCorrectText() {
    val strings = rule.setContentWithLocalizedStrings { newContestButton(onClick = {}) }
    rule.onNodeWithText(strings.newContest).assertIsDisplayed()
  }

  @Test
  fun given_contestScreen_when_displayed_then_titleIsDisplayed() {
    val state = FakeContestScreenState()
    val strings = rule.setContentWithLocalizedStrings { ContestScreen(state) }
    rule.onNodeWithText(strings.tournamentContestsTitle).assertIsDisplayed()
  }
}
