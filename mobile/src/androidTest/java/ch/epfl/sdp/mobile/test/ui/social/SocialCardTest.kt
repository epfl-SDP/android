package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.PersonItem
import org.junit.Rule
import org.junit.Test

class SocialCardTest {
  @get:Rule val rule = createComposeRule()

  private class FakeFriendCard : Person {
    override val backgroundColor: Color = Color.Default
    override val name: String = "Toto"
    override val emoji: String = ":3"
    override val followed: Boolean = false
  }

  @Test
  fun card_displayCorrectName() {

    rule.setContent { PersonItem(person = FakeFriendCard()) }

    rule.onNodeWithText("Toto").assertExists()
  }

  @Test
  fun card_displayCorrectEmoji() {
    rule.setContent { PersonItem(person = FakeFriendCard()) }

    rule.onNodeWithText(":3").assertExists()
  }
}
