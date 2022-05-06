package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.Badge
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import org.junit.Rule
import org.junit.Test

class BadgeTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_joinBadge_when_displayed_then_showsCorrectText() {
    val strings = rule.setContentWithLocalizedStrings { Badge(BadgeType.Join, onClick = {}) }
    rule.onNodeWithText(strings.tournamentsBadgeJoin).assertIsDisplayed()
  }

  @Test
  fun given_participantBadge_when_displayed_then_showsCorrectText() {
    val strings = rule.setContentWithLocalizedStrings { Badge(BadgeType.Participant, onClick = {}) }
    rule.onNodeWithText(strings.tournamentsBadgeParticipant).assertIsDisplayed()
  }

  @Test
  fun given_adminBadge_when_displayed_then_showsCorrectText() {
    val strings = rule.setContentWithLocalizedStrings { Badge(BadgeType.Admin, onClick = {}) }
    rule.onNodeWithText(strings.tournamentsBadgeAdmin).assertIsDisplayed()
  }

  @Test
  fun given_enabledBadge_when_clicking_then_callsCallback() {
    val channel = Channel<Unit>(1)
    val strings =
        rule.setContentWithLocalizedStrings {
          Badge(
              BadgeType.Admin,
              onClick = {
                channel.trySend(Unit)
                channel.close()
              },
          )
        }
    rule.onNodeWithText(strings.tournamentsBadgeAdmin).performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
    assertThat(channel.tryReceive().isClosed).isTrue()
  }

  @Test
  fun given_disabled_when_clicking_then_doesNotCallCallback() {
    val channel = Channel<Unit>(1)
    val strings =
        rule.setContentWithLocalizedStrings {
          Badge(BadgeType.Admin, onClick = { channel.trySend(Unit) }, enabled = false)
        }
    rule.onNodeWithText(strings.tournamentsBadgeAdmin).performClick()
    assertThat(channel.tryReceive().isSuccess).isFalse()
  }

  @Test
  fun given_badge_when_changingType_then_displaysRightText() {
    var type by mutableStateOf(BadgeType.Admin)
    val strings = rule.setContentWithLocalizedStrings { Badge(type, onClick = {}) }
    type = BadgeType.Join
    rule.onNodeWithText(strings.tournamentsBadgeJoin).assertIsDisplayed()
  }
}
