package ch.epfl.sdp.mobile.test.state.tournaments

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.state.tournaments.StatefulTournamentDetailsScreen
import ch.epfl.sdp.mobile.state.tournaments.TournamentDetailsActions
import ch.epfl.sdp.mobile.test.state.setContentWithTestEnvironment
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulTournamentDetailsScreenTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulTournamentDetails_when_clickingBack_then_callsCallback() = runTest {
    val channel = Channel<Unit>(1)
    val env =
        rule.setContentWithTestEnvironment {
          StatefulTournamentDetailsScreen(
              user,
              TournamentReference(""),
              TournamentDetailsActions(onBackClick = { channel.trySend(Unit) }),
          )
        }
    rule.onNodeWithContentDescription(env.strings.tournamentDetailsBackContentDescription)
        .performClick()
    assertThat(channel.tryReceive().getOrNull()).isNotNull()
  }
}
