package ch.epfl.sdp.mobile.test

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.state.DebounceDuration

/**
 * Wait until a certain text is visible before clicking on it
 *
 * @receiver the [ComposeTestRule] that will be waiting.
 * @param text the text to match.
 */
fun ComposeTestRule.performClickOnceVisible(text: String) {
  this.waitUntil(timeoutMillis = 2 * DebounceDuration.inWholeMilliseconds) {
    onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
  }
  onNodeWithText(text).performClick()
}
