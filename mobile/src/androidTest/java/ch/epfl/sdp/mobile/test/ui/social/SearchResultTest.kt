package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.social.*
import org.junit.Rule
import org.junit.Test

class SearchResultTest {
  @get:Rule val rule = createComposeRule()

  private class SnapshotSearchState : SearchState {
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
  fun list_displayAllUserInState() {

    val state = SnapshotSearchState()

    rule.setContent { SearchResultList(state = state) }

    this.rule.onRoot().onChild().onChildren().assertCountEquals(4)
  }

  @Test
  fun card_hasFollowText() {
    val state = SnapshotSearchState()

    val strings = rule.setContentWithLocalizedStrings { SearchResultList(state = state) }

    rule.onAllNodesWithText(strings.socialFollow.uppercase()).onFirst().assertExists()
  }
}
