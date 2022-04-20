package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.sharedTest.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.social.SearchField
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchFieldTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun typingText_displaysText() {
    rule.setContent {
      val (value, setValue) = remember { mutableStateOf("") }
      SearchField(value, setValue, Modifier.testTag("search"))
    }
    rule.onNodeWithTag("search").performTextInput("Hello world")

    rule.onNodeWithText("Hello world").assertExists()
  }

  @Test
  fun clickingClearIcon_clearsText() {
    val strings =
        rule.setContentWithLocalizedStrings {
          val (value, setValue) = remember { mutableStateOf("") }
          SearchField(value, setValue, Modifier.testTag("search"))
        }
    rule.onNodeWithTag("search").performTextInput("Hello world")
    rule.onNodeWithContentDescription(strings.socialSearchClearContentDescription).performClick()

    rule.onNodeWithText(strings.socialSearchBarPlaceHolder).assertExists()
  }
}
