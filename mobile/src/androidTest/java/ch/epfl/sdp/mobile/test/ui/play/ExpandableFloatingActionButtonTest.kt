package ch.epfl.sdp.mobile.test.ui.play

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.ui.play.ExpandableFloatingActionButton
import ch.epfl.sdp.mobile.ui.play.ExpandableFloatingActionButtonState
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class ExpandableFloatingActionButtonTest {

  @get:Rule val rule = createComposeRule()

  private class SnapshotExpandableFloatingActionButtonState(
      initiallyExpanded: Boolean = false,
  ) : ExpandableFloatingActionButtonState {
    override var expanded by mutableStateOf(initiallyExpanded)
  }

  @Test
  fun clickingButton_expandsIt() {
    val state = SnapshotExpandableFloatingActionButtonState()
    rule.setContent {
      ExpandableFloatingActionButton(
          state = state,
          expandedContent = { Text("Expanded") },
          collapsedContent = { Text("Collapsed") },
      )
    }
    rule.onNodeWithText("Collapsed").performClick()
    rule.onNodeWithText("Expanded").assertIsDisplayed()
    assertThat(state.expanded).isTrue()
  }
}
