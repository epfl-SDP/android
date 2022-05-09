package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.*
import ch.epfl.sdp.mobile.ui.tournaments.Status.ContestStatus.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import org.junit.Rule
import org.junit.Test

open class FakeContestScreenState : ContestScreenState<Contest> {
  override val contests: List<Contest> =
      listOf(createContest("Name", 1.days, ONGOING, BadgeType.Admin))

  override fun onNewContestClick() = Unit
  override fun onContestClick(C: Contest) = Unit
}

private fun createContest(
    name: String,
    duration: Duration,
    status: Status.ContestStatus,
    personStatus: BadgeType
): Contest {
  return object : Contest {
    override val name = name
    override val creationTime = duration
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
  fun given_contestScreen_when_oneContestRegistered_then_contestIsDisplayed() {
    val state = FakeContestScreenState()
    val strings = rule.setContentWithLocalizedStrings { ContestScreen(state) }
    rule.onNodeWithText("Name").assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsBadgeAdmin).assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsStartingTime(1.days)).assertIsDisplayed()
  }
}
