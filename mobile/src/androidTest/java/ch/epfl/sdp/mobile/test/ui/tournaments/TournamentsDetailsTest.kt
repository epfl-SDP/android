package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import org.junit.Rule
import org.junit.Test

class TournamentsDetailsTest {

  @get:Rule val rule = createComposeRule()

  object EmptyPoolInfo : PoolInfo<PoolMember> {
    override val name = "Pool #1"
    override val status = PoolInfo.Status.StillOpen
    override val startNextRoundEnabled = false
    override fun onStartNextRound() = Unit
    override val members = emptyList<PoolMember>()
    override fun PoolMember.scoreAgainst(other: PoolMember): PoolScore? = null
  }

  class OneMatchTournamentDetails(
      val match: TournamentMatch,
  ) : TournamentDetailsState<PoolMember, TournamentMatch> {
    var clicked = false
    override val badge: BadgeType? = null
    override val title = ""
    override val pools = listOf(EmptyPoolInfo)
    override val finals = listOf(TournamentsFinalsRound("TAB", listOf(match)))
    override fun onBadgeClick() = Unit
    override fun onWatchMatchClick(match: TournamentMatch) {
      clicked = true
    }
    override fun onCloseClick() = Unit
  }

  @Test
  fun given_tournamentsDetails_when_started_then_DisplaysPoolsTab() {
    val details =
        object : TournamentDetailsState<PoolMember, TournamentMatch> {
          override val badge: BadgeType? = null
          override val title = ""
          override val pools = listOf(EmptyPoolInfo)
          override val finals = emptyList<TournamentsFinalsRound<TournamentMatch>>()
          override fun onBadgeClick() = Unit
          override fun onWatchMatchClick(match: TournamentMatch) = Unit
          override fun onCloseClick() = Unit
        }
    rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("Pool #1").assertIsDisplayed()
  }

  @Test
  fun given_tournamentsDetails_when_clickingOnJoinBadge_then_CallsCallback() {
    val channel = Channel<Unit>(1)
    val details =
        object : TournamentDetailsState<PoolMember, TournamentMatch> {
          override val badge = BadgeType.Join
          override val title = ""
          override val pools = listOf(EmptyPoolInfo)
          override val finals = emptyList<TournamentsFinalsRound<TournamentMatch>>()
          override fun onBadgeClick() {
            channel.trySend(Unit)
          }
          override fun onWatchMatchClick(match: TournamentMatch) = Unit
          override fun onCloseClick() = Unit
        }
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText(strings.tournamentsBadgeJoin).performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_oneOngoingMatch_when_clickingWatch_then_callCallback() {
    val match =
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result = TournamentMatch.Result.Ongoing
        }
    val details = OneMatchTournamentDetails(match)
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("TAB").performClick()
    rule.onNodeWithText(strings.tournamentsDetailsWatch).performClick()
    assertThat(details.clicked).isTrue()
  }

  @Test
  fun given_oneDrawnMatch_when_displayingFinals_then_displaysCorrectText() {
    val match =
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result = TournamentMatch.Result.Draw
        }
    val details = OneMatchTournamentDetails(match)
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("TAB").performClick()
    rule.onNodeWithText(strings.tournamentsDetailsMatchDrawn).assertIsDisplayed()
  }

  @Test
  fun given_oneWonForFirstMatch_when_displayingFinals_then_displaysCorrectText() {
    val match =
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result = TournamentMatch.Result.FirstWon
        }
    val details = OneMatchTournamentDetails(match)
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("TAB").performClick()
    rule.onNodeWithText(strings.tournamentsDetailsMatchWon).assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsDetailsMatchLost).assertIsDisplayed()
  }

  @Test
  fun given_oneLostForFirstMatch_when_displayingFinals_then_displaysCorrectText() {
    val match =
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result = TournamentMatch.Result.SecondWon
        }
    val details = OneMatchTournamentDetails(match)
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("TAB").performClick()
    rule.onNodeWithText(strings.tournamentsDetailsMatchWon).assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsDetailsMatchLost).assertIsDisplayed()
  }
}