package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.FiltersDialog
import ch.epfl.sdp.mobile.ui.tournaments.FiltersDialogState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class FiltersDialogTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_dialog_when_clicksDone_then_callbackIsCalled() {
    val state = mockk<FiltersDialogState>(relaxed = true)
    val strings = rule.setContentWithLocalizedStrings { FiltersDialog(state) }

    rule.onNodeWithText(strings.tournamentsFilterOnlyDone).performClick()
    verify { state.onShowDoneClick() }
  }

  @Test
  fun given_dialog_when_clicksParticipating_then_callbackIsCalled() {
    val state = mockk<FiltersDialogState>(relaxed = true)
    val strings = rule.setContentWithLocalizedStrings { FiltersDialog(state) }

    rule.onNodeWithText(strings.tournamentsFilterOnlyParticipating).performClick()
    verify { state.onShowParticipatingClick() }
  }

  @Test
  fun given_dialog_when_clicksAdministrating_then_callbackIsCalled() {
    val state = mockk<FiltersDialogState>(relaxed = true)
    val strings = rule.setContentWithLocalizedStrings { FiltersDialog(state) }

    rule.onNodeWithText(strings.tournamentsFilterOnlyAdministrating).performClick()
    verify { state.onShowAdministratingClick() }
  }
}
