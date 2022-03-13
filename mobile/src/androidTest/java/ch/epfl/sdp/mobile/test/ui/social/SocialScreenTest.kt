package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ProfileColor
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
            createPerson(ProfileColor.Pink, "Toto", ":)"),
            createPerson(ProfileColor.Pink, "John", ":3"),
            createPerson(ProfileColor.Pink, "Travis", ";)"),
            createPerson(ProfileColor.Pink, "Cirrus", "TwT"))

    companion object {
      fun createPerson(bgColor: ProfileColor, name: String, emoji: String): Person {
        return object : Person {
          override val backgroundColor: ProfileColor = bgColor
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
}
