package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.social.FollowingState
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import org.junit.Rule
import org.junit.Test

class SocialScreenTest {
  @get:Rule val rule = createComposeRule()

  private class SnapshotSocialScreenState : FollowingState {
    override val players: List<Person> =
        listOf(
            createPerson(Color.Default, "Toto", ":)"),
            createPerson(Color.Default, "John", ":3"),
            createPerson(Color.Default, "Travis", ";)"),
            createPerson(Color.Default, "Cirrus", "TwT"))

    companion object {
      fun createPerson(bgColor: Color, name: String, emoji: String): Person {
        return object : Person {
          override val backgroundColor: Color = bgColor
          override val name: String = name
          override val emoji: String = emoji
        }
      }
    }
  }

  @Test
  fun title_isDisplay() {
    val strings =
        rule.setContentWithLocalizedStrings { SocialScreen(state = SnapshotSocialScreenState()) }

    rule.onNodeWithText(strings.socialFollowingTitle).assertExists()
  }

  @Test
  fun list_displayAllUser() {

    val state = SnapshotSocialScreenState()

    rule.setContent { SocialScreen(state = state) }

    rule.onNodeWithTag("friendList").onChildren().assertCountEquals(4)
  }

  @Test
  fun card_hasPlayButton() {

    val state = SnapshotSocialScreenState()

    val strings = rule.setContentWithLocalizedStrings { SocialScreen(state = state) }

    rule.onAllNodesWithText(strings.socialPerformPlay.uppercase()).onFirst().assertExists()
  }
}
