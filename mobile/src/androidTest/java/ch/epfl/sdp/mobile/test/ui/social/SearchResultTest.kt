package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.social.*
import org.junit.Rule
import org.junit.Test

class SearchResultTest {
  @get:Rule val rule = createComposeRule()

  private fun createPerson(bgColor: Color, name: String, emoji: String): Person {
    return object : Person {
      override val backgroundColor: Color = bgColor
      override val name: String = name
      override val emoji: String = emoji
    }
  }

  @Test
  fun list_displayAllUserInState() {

    val list =
        listOf(
            createPerson(Color.Default, "Toto", ":)"),
            createPerson(Color.Default, "John", ":3"),
            createPerson(Color.Default, "Travis", ";)"),
            createPerson(Color.Default, "Cirrus", "TwT"))

    rule.setContent { SearchResultList(list) }

    this.rule.onRoot().onChild().onChildren().assertCountEquals(4)
  }

  @Test
  fun card_hasFollowText() {
    val list =
        listOf(
            createPerson(Color.Default, "Toto", ":)"),
            createPerson(Color.Default, "John", ":3"),
            createPerson(Color.Default, "Travis", ";)"),
            createPerson(Color.Default, "Cirrus", "TwT"))

    val strings = rule.setContentWithLocalizedStrings { SearchResultList(list) }

    rule.onAllNodesWithText(strings.socialFollow.uppercase()).onFirst().assertExists()
  }
}
