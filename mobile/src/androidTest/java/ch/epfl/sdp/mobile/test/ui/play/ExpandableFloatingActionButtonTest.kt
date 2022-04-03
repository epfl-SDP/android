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
import ch.epfl.sdp.mobile.ui.play.ExpandableFloatingActionButtonItem
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class ExpandableFloatingActionButtonTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun clickingButton_expandsIt() {
    rule.setContent {
      ExpandableFloatingActionButton(
          expandedContent = { Text("Expanded") },
          collapsedContent = { Text("Collapsed") },
      )
    }
    rule.onNodeWithText("Collapsed").performClick()
    rule.onNodeWithText("Expanded").assertIsDisplayed()
  }

  @Test
  fun item_detectsClicks() {
    var clicked by mutableStateOf(false)
    rule.setContent {
      ExpandableFloatingActionButtonItem(
          onClick = { clicked = true },
          icon = {},
          text = { Text("Item") },
      )
    }
    rule.onNodeWithText("Item").performClick()
    assertThat(clicked).isTrue()
  }
}
