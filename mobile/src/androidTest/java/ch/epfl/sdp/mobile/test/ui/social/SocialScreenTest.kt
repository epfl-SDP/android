package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.English.socialFollow
import ch.epfl.sdp.mobile.ui.i18n.English.socialSearchBarPlaceHolder
import ch.epfl.sdp.mobile.ui.i18n.English.socialSearchEmptyTitle
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SearchResultList
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import org.junit.Rule
import org.junit.Test

class SocialScreenTest {
  @get:Rule val rule = createComposeRule()

  private class SnapshotSocialScreenState : SocialScreenState {
    override var searchResult: List<Person> = emptyList()
    override var mode: SocialScreenState.Mode by mutableStateOf(Following)
    override var following: List<Person> =
        listOf(
            createPerson(Color.Default, "Toto", ":)"),
            createPerson(Color.Default, "John", ":3"),
            createPerson(Color.Default, "Travis", ";)"),
            createPerson(Color.Default, "Cirrus", "TwT"))
    override var input: String by mutableStateOf("")
    override var searchFieldInteraction: MutableInteractionSource
      get() = MutableInteractionSource()
      set(value) {}

    override fun onValueChange() {
      mode = Searching
    }

    override fun onFollow(followed: Person) {
      TODO("Not yet implemented")
    }

    companion object {
      fun createPerson(bgColor: Color, name: String, emoji: String): Person {
        return object : Person {
          override val backgroundColor: Color = bgColor
          override val name: String = name
          override val emoji: String = emoji
          override val uid: String
            get() = TODO("Not yet implemented")
        }
      }
    }
  }

  @Test
  fun defaultMode_isFollowing() {
    val state = SnapshotSocialScreenState()
    rule.setContentWithLocalizedStrings { SocialScreen(state) }

    rule.onNodeWithText(socialFollow).assertDoesNotExist()
  }

  @Test
  fun type_switchMode() {
    val state = SnapshotSocialScreenState()
    state.searchResult =
        listOf(
            object : Person {
              override val backgroundColor = Color.Default
              override val name = "test"
              override val emoji = ":)"
              override val uid = ""
            })
    rule.setContentWithLocalizedStrings { SocialScreen(state) }
    rule.onRoot().performTouchInput { swipeUp() }

    rule.onNodeWithText(socialSearchBarPlaceHolder).performTextInput("test")

    rule.onAllNodesWithText(socialFollow).onFirst().assertExists()
  }

  @Test
  fun searchMode_emptyInputScreen() {
    val state = SnapshotSocialScreenState()
    rule.setContentWithLocalizedStrings { SocialScreen(state) }
    rule.onRoot().performTouchInput { swipeUp() }

    val inputString = "test"
    rule.onNodeWithText(socialSearchBarPlaceHolder).performTextInput(inputString)
    rule.onNodeWithText(inputString).performTextClearance()

    rule.onNodeWithText(socialSearchEmptyTitle).assertExists()
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

  @Test
  fun searchList_displayAllUserInState() {

    val state = SnapshotSocialScreenState()

    rule.setContent { SearchResultList(state.following, onClick = { p -> {} }) }

    this.rule.onRoot().onChild().onChildren().assertCountEquals(4)
  }

  @Test
  fun searchList_hasFollowText() {
    val state = SnapshotSocialScreenState()
    val strings =
        rule.setContentWithLocalizedStrings {
          SearchResultList(
              listOf(
                  object : Person {
                    override val backgroundColor = Color.Default
                    override val name = "test"
                    override val emoji = ":)"
                    override val uid = ""
                  }),
              onClick = { p -> {} })
        }

    rule.onAllNodesWithText(strings.socialFollow.uppercase()).onFirst().assertExists()
  }
}
