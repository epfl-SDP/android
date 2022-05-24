package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.SpanStyle
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import org.junit.Rule
import org.junit.Test

class ContestScreenTest {

  private class FakeContestScreenState : ContestScreenState<ContestInfo> {
    override fun onNewContestClick() = Unit
    override fun onContestClick(contest: ContestInfo) = Unit
    override fun onFilterClick() = Unit
    override val contests =
        listOf(
            createContest("Name", ContestInfo.Status.Ongoing(1.days), BadgeType.Admin),
            createContest("Name 2", ContestInfo.Status.Ongoing(3.hours), BadgeType.Participant))

    private fun createContest(
        name: String,
        status: ContestInfo.Status,
        personStatus: BadgeType
    ): ContestInfo {
      return object : ContestInfo {
        override val name = name
        override val badge = personStatus
        override val status = status
      }
    }
  }

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_contestScreen_when_displayed_then_showsCorrectButtonText() {
    val state = FakeContestScreenState()
    val strings = rule.setContentWithLocalizedStrings { ContestScreen(state) }
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
    rule.onNodeWithText(strings.tournamentsStartingTime(1.days, SpanStyle()).text)
        .assertIsDisplayed()
    rule.onNodeWithText("Name 2").assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsBadgeParticipant).assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsStartingTime(3.hours, SpanStyle()).text)
        .assertIsDisplayed()
  }
}
