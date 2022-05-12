package ch.epfl.sdp.mobile.test.state.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.state.tournaments.StatefulTournamentDetailsScreen
import ch.epfl.sdp.mobile.state.tournaments.TournamentDetailsActions
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
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
              user = user,
              reference = TournamentReference(""),
              actions = TournamentDetailsActions(onBackClick = { channel.trySend(Unit) }),
          )
        }
    rule.onNodeWithContentDescription(env.strings.tournamentDetailsBackContentDescription)
        .performClick()
    assertThat(channel.tryReceive().getOrNull()).isNotNull()
  }

  @Test
  fun given_statefulTournamentDetails_when_displayingTournament_then_showsRightTitle() = runTest {
    val reference = TournamentReference("1")
    val store = buildStore {
      collection(TournamentDocument.Collection) {
        document(reference.uid, TournamentDocument(name = "Sample"))
      }
    }
    rule.setContentWithTestEnvironment(store = store) {
      StatefulTournamentDetailsScreen(
          user = user,
          reference = reference,
          actions = TournamentDetailsActions(onBackClick = {}),
      )
    }
    rule.onNodeWithText("Sample", ignoreCase = true).assertIsDisplayed()
  }
}
