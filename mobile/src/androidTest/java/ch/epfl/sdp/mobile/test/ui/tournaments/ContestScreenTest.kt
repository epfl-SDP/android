package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.*
import ch.epfl.sdp.mobile.ui.tournaments.ContestPersonStatus.*
import ch.epfl.sdp.mobile.ui.tournaments.ContestStatus.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import org.junit.Rule
import org.junit.Test

open class FakeContestScreenState : ContestScreenState {
  override val contests: List<Contest> = listOf(createContest("name", 1.days, ONGOING, ADMIN))

  override fun onNewContestClick() = Unit
}

private fun createContest(
    name: String,
    duration: Duration,
    status: ContestStatus,
    personStatus: ContestPersonStatus
): Contest {
  return object : Contest {
    override val name = name
    override val creationDate = duration
    override val personStatus = personStatus
    override val status = status
  }
}

class NewContestButtonTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_newContestButton_when_displayed_then_showsCorrectText() {
    val strings = rule.setContentWithLocalizedStrings { NewContestButton(onClick = {}) }
    rule.onNodeWithText(strings.newContest).assertIsDisplayed()
  }

  @Test
  fun given_contestScreen_when_displayed_then_titleIsDisplayed() {
    val state = FakeContestScreenState()
    val strings = rule.setContentWithLocalizedStrings { ContestScreen(state) }
    rule.onNodeWithText(strings.tournamentsContestsTitle).assertIsDisplayed()
  }

  @Test
  fun given_contestScreen_when_oneGameRegistered_then_gameIsDisplayed() {
    val state = FakeContestScreenState()
    val strings = rule.setContentWithLocalizedStrings { ContestScreen(state) }
    rule.onNodeWithText("name").assertIsDisplayed()
  }
}
