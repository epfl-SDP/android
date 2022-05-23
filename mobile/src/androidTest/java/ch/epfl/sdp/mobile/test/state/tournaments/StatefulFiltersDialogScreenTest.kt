package ch.epfl.sdp.mobile.test.state.tournaments

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.state.tournaments.StatefulFiltersDialogScreen
import ch.epfl.sdp.mobile.test.state.setContentWithTestEnvironment
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulFiltersDialogScreenTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_dialog_when_clickingDone_then_setsDoneToTrue() = runTest {
    val (facades, _, strings) =
        rule.setContentWithTestEnvironment { StatefulFiltersDialogScreen({}) }
    rule.onNodeWithText(strings.tournamentsFilterOnlyDone).performClick()
    val filters = facades.tournaments.filters().first()

    assertThat(filters.showDone).isTrue()
  }

  @Test
  fun given_dialog_when_clickingParticipating_then_setsParticipatingToTrue() = runTest {
    val (facades, _, strings) =
        rule.setContentWithTestEnvironment { StatefulFiltersDialogScreen({}) }
    rule.onNodeWithText(strings.tournamentsFilterOnlyParticipating).performClick()
    val filters = facades.tournaments.filters().first()

    assertThat(filters.showParticipating).isTrue()
  }

  @Test
  fun given_dialog_when_clickingAdministrating_then_setsAdministratingToTrue() = runTest {
    val (facades, _, strings) =
        rule.setContentWithTestEnvironment { StatefulFiltersDialogScreen({}) }
    rule.onNodeWithText(strings.tournamentsFilterOnlyAdministrating).performClick()
    val filters = facades.tournaments.filters().first()

    assertThat(filters.showAdministrating).isTrue()
  }
}
