package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.English.socialFollow
import ch.epfl.sdp.mobile.ui.i18n.English.socialPerformPlay
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialCard
import ch.epfl.sdp.mobile.ui.social.SocialMode
import org.junit.Rule
import org.junit.Test

class SocialCardTest {
  @get:Rule val rule = createComposeRule()

  private class SnapshotFriendCard : Person {
    override val backgroundColor: ProfileColor = ProfileColor.Pink
    override val name: String = "Toto"
    override val emoji: String = ":3"
  }

  @Test
  fun card_displayCorrectName() {

    rule.setContent { SocialCard(person = SnapshotFriendCard(), SocialMode.Play) }

    rule.onNodeWithText("Toto").assertExists()
  }

  @Test
  fun card_displayCorrectEmoji() {
    rule.setContent { SocialCard(person = SnapshotFriendCard(), SocialMode.Play) }

    rule.onNodeWithText(":3").assertExists()
  }

  @Test
  fun card_showPlayWhenPlayMode() {
    rule.setContentWithLocalizedStrings {
      SocialCard(person = SnapshotFriendCard(), SocialMode.Play)
    }

    rule.onNodeWithText(socialPerformPlay.uppercase()).assertExists()
  }

  @Test
  fun card_showPlayWhenFollowMode() {
    rule.setContentWithLocalizedStrings {
      SocialCard(person = SnapshotFriendCard(), SocialMode.Follow)
    }

    rule.onNodeWithText(socialFollow.uppercase()).assertExists()
  }
}
