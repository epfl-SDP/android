package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.*
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

  @Test
  fun given_tournamentsDetails_when_started_thenDisplaysPoolsTab() {
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
}
