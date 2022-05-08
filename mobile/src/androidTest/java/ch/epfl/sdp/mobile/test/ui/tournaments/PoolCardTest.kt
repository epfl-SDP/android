package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.PoolCard
import ch.epfl.sdp.mobile.ui.tournaments.PoolInfo
import ch.epfl.sdp.mobile.ui.tournaments.PoolMember
import ch.epfl.sdp.mobile.ui.tournaments.PoolScore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import org.junit.Rule
import org.junit.Test

class PoolCardTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_nextRoundEnabled_when_clickingStartRound_then_callsCallback() {
    val channel = Channel<Unit>(1)
    val info =
        object : PoolInfo<PoolMember> {
          override val name = ""
          override val status = PoolInfo.Status.StillOpen
          override val startNextRoundEnabled = true
          override fun onStartNextRound() {
            channel.trySend(Unit)
          }
          override val members = emptyList<PoolMember>()
          override fun PoolMember.scoreAgainst(other: PoolMember) = 0
        }
    val strings = rule.setContentWithLocalizedStrings { PoolCard(info) }
    rule.onNodeWithText(strings.tournamentsPoolStartNextRound).performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_nextRoundEnabled_when_displayed_then_hasNoAction() {
    val info =
        object : PoolInfo<PoolMember> {
          override val name = ""
          override val status = PoolInfo.Status.StillOpen
          override val startNextRoundEnabled = false
          override fun onStartNextRound() = Unit
          override val members = emptyList<PoolMember>()
          override fun PoolMember.scoreAgainst(other: PoolMember) = 0
        }
    val strings = rule.setContentWithLocalizedStrings { PoolCard(info) }
    rule.onNodeWithText(strings.tournamentsPoolStartNextRound).assertDoesNotExist()
  }

  @Test
  fun given_emptyPool_when_stillOpen_then_hasCorrectText() {
    val info =
        object : PoolInfo<PoolMember> {
          override val name = ""
          override val status = PoolInfo.Status.StillOpen
          override val startNextRoundEnabled = false
          override fun onStartNextRound() = Unit
          override val members = emptyList<PoolMember>()
          override fun PoolMember.scoreAgainst(other: PoolMember) = 0
        }
    val strings = rule.setContentWithLocalizedStrings { PoolCard(info) }
    rule.onNodeWithText(strings.tournamentsPoolStillOpen).assertIsDisplayed()
  }

  @Test
  fun given_emptyPool_when_ongoingRounds_then_hasCorrectText() {
    val info =
        object : PoolInfo<PoolMember> {
          override val name = ""
          override val status = PoolInfo.Status.Ongoing(4, 7)
          override val startNextRoundEnabled = false
          override fun onStartNextRound() = Unit
          override val members = emptyList<PoolMember>()
          override fun PoolMember.scoreAgainst(other: PoolMember) = 0
        }
    val strings = rule.setContentWithLocalizedStrings { PoolCard(info) }
    rule.onNodeWithText(strings.tournamentsPoolRound(4, 7)).assertIsDisplayed()
  }

  @Test
  fun given_emptyPool_when_displayed_then_hasCorrectTitle() {
    val info =
        object : PoolInfo<PoolMember> {
          override val name = "Hello there"
          override val status = PoolInfo.Status.StillOpen
          override val startNextRoundEnabled = false
          override fun onStartNextRound() = Unit
          override val members = emptyList<PoolMember>()
          override fun PoolMember.scoreAgainst(other: PoolMember) = 0
        }
    rule.setContentWithLocalizedStrings { PoolCard(info) }
    rule.onNodeWithText("Hello there").assertIsDisplayed()
  }

  @Test
  fun given_nonEmptyPool_whenDisplayed_then_showsAllPlayersTwice() {
    val info =
        object : PoolInfo<PoolMember> {
          override val name = "Hello there"
          override val status = PoolInfo.Status.StillOpen
          override val startNextRoundEnabled = false
          override fun onStartNextRound() = Unit
          override val members =
              listOf(
                  object : PoolMember {
                    override val name: String = "Alexandre"
                    override val total: PoolScore? = null
                  },
                  object : PoolMember {
                    override val name: String = "Georges"
                    override val total: PoolScore? = null
                  },
              )
          override fun PoolMember.scoreAgainst(other: PoolMember) = 0
        }
    rule.setContentWithLocalizedStrings { PoolCard(info) }
    rule.onAllNodesWithText("Alexandre", ignoreCase = true).assertCountEquals(2)
    rule.onAllNodesWithText("Georges", ignoreCase = true).assertCountEquals(2)
  }
}
