package ch.epfl.sdp.mobile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ExampleTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun pressedButton_updatesCount() {
    rule.setContent { Example() }
    rule.onNodeWithText("Pressed 0 times").performClick()
    rule.onNodeWithText("Pressed 1 times").assertExists()
  }

  @Test
  fun unpressedButton_hidesText() {
    rule.setContent { Example() }
    rule.onNodeWithText("Yay").assertDoesNotExist()
  }

  @Test
  fun pressedTwiceButton_showsExtraText() {
    rule.setContent { Example() }
    repeat(2) { rule.onNodeWithText("Pressed", substring = true).performClick() }
    rule.onNodeWithText("Yay").assertExists()
  }
}
