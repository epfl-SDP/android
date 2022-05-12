package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.*
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.StartTournamentBanner
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.StartTournamentBanner.EnoughPlayers
import ch.epfl.sdp.mobile.ui.tournaments.TournamentDetailsState.StartTournamentBanner.NotEnoughPlayers
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

  class TABTournamentsFinalRound(
      match: TournamentMatch,
      override val banner: TournamentsFinalsRound.Banner?,
  ) : TournamentsFinalsRound<TournamentMatch> {
    override val name = "TAB"
    override val matches = listOf(match)
    override fun onBannerClick() = Unit
  }

  class OneMatchTournamentDetails(
      val match: TournamentMatch,
      banner: TournamentsFinalsRound.Banner? = null,
  ) : TournamentDetailsState<PoolMember, TournamentMatch> {
    var clicked = false
    override val badge: BadgeType? = null
    override val title = ""
    override val pools = listOf(EmptyPoolInfo)
    override val finals = listOf(TABTournamentsFinalRound(match, banner))
    override val startTournamentBanner = EnoughPlayers
    override fun onStartTournament() = Unit
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
          override val startTournamentBanner = NotEnoughPlayers
          override fun onStartTournament() = Unit
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
          override val startTournamentBanner: StartTournamentBanner? = null
          override fun onStartTournament() = Unit
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

  @Test
  fun given_greenBanner_when_displayingFinals_then_displaysCorrectText() {
    val match =
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result = TournamentMatch.Result.SecondWon
        }
    val details = OneMatchTournamentDetails(match, TournamentsFinalsRound.Banner.NextBestOf(1, 2))
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("TAB").performClick()
    rule.onNodeWithText(strings.tournamentsDetailsNextBestOfTitle(1, 2)).assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsDetailsNextBestOfSubtitle).assertIsDisplayed()
  }

  @Test
  fun given_orangeBanner_when_displayingFinals_then_displaysCorrectText() {
    val match =
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result = TournamentMatch.Result.SecondWon
        }
    val details = OneMatchTournamentDetails(match, TournamentsFinalsRound.Banner.NextRound)
    val strings = rule.setContentWithLocalizedStrings { TournamentDetails(details) }
    rule.onNodeWithText("TAB").performClick()
    rule.onNodeWithText(strings.tournamentsDetailsNextRoundTitle).assertIsDisplayed()
    rule.onNodeWithText(strings.tournamentsDetailsNextRoundSubtitle).assertIsDisplayed()
  }
}
