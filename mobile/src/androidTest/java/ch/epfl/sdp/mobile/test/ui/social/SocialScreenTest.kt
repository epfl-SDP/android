package ch.epfl.sdp.mobile.test.ui.social

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.English.socialPerformFollow
import ch.epfl.sdp.mobile.ui.i18n.English.socialSearchEmptyTitle
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SearchResultList
import ch.epfl.sdp.mobile.ui.social.SocialScreen
import ch.epfl.sdp.mobile.ui.social.SocialScreenState
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Following
import ch.epfl.sdp.mobile.ui.social.SocialScreenState.Mode.Searching
import org.junit.Rule
import org.junit.Test

private val People =
    listOf(
        createPerson(Color.Default, "Toto", ":)"),
        createPerson(Color.Default, "John", ":3"),
        createPerson(Color.Default, "Travis", ";)"),
        createPerson(Color.Default, "Cirrus", "TwT"),
    )

private fun createPerson(bgColor: Color, name: String, emoji: String): Person {
  return object : Person {
    override val backgroundColor: Color = bgColor
    override val name: String = name
    override val emoji: String = emoji
    override val followed = false
  }
}

class SocialScreenTest {
  @get:Rule val rule = createComposeRule()

  private class FakeSnapshotSocialScreenState : SocialScreenState<Person> {
    override var searchResult: List<Person> = emptyList()
    override var mode: SocialScreenState.Mode by mutableStateOf(Following)
    override var following: List<Person> = People

    override var input: String by mutableStateOf("")
    override val searchFieldInteraction = MutableInteractionSource()

    override fun onFollowClick(followed: Person) = Unit

    companion object {}

    override fun onShowProfileClick(person: Person) {}
  }

  @Test
  fun defaultMode_isFollowing() {
    val state = FakeSnapshotSocialScreenState()
    rule.setContentWithLocalizedStrings { SocialScreen(state) }

    rule.onNodeWithText(socialPerformFollow).assertDoesNotExist()
  }

  @Test
  fun searchMode_emptyInputScreen() {
    val state = FakeSnapshotSocialScreenState()
    state.mode = Searching
    rule.setContentWithLocalizedStrings { SocialScreen(state) }
    rule.onRoot().performTouchInput { swipeUp() }

    rule.onNodeWithText(socialSearchEmptyTitle).assertExists()
  }

  @Test
  fun title_isDisplay() {
    val strings =
        rule.setContentWithLocalizedStrings {
          SocialScreen(state = FakeSnapshotSocialScreenState())
        }

    rule.onNodeWithText(strings.socialFollowingTitle).assertExists()
  }

  @Test
  fun list_displayAllUser() {

    val state = FakeSnapshotSocialScreenState()

    rule.setContent { SocialScreen(state = state) }

    People.forEach { rule.onNodeWithText(it.name).assertExists() }
  }

  @Test
  fun card_hasPlayButton() {

    val state = FakeSnapshotSocialScreenState()

    val strings = rule.setContentWithLocalizedStrings { SocialScreen(state = state) }

    rule.onAllNodesWithText(strings.socialPerformPlay.uppercase()).onFirst().assertExists()
  }

  @Test
  fun searchList_displayAllUserInState() {

    val state = FakeSnapshotSocialScreenState()

    rule.setContent {
      SearchResultList(state.following, onFollowClick = {}, onShowProfileClick = {})
    }

    People.forEach { rule.onNodeWithText(it.name).assertExists() }
  }

  @Test
  fun searchList_hasFollowText() {
    val strings =
        rule.setContentWithLocalizedStrings {
          SearchResultList(
              listOf(
                  object : Person {
                    override val backgroundColor = Color.Default
                    override val name = "test"
                    override val emoji = ":)"
                    override val followed = false
                  }),
              onFollowClick = {},
              onShowProfileClick = {})
        }

    rule.onAllNodesWithText(strings.socialPerformFollow.uppercase()).onFirst().assertExists()
  }

  @Test
  fun list_displayAllUsers() {

    val state = FakeSnapshotSocialScreenState()

    rule.setContent { SocialScreen(state = state) }

    People.forEach { rule.onNodeWithText(it.name).assertExists() }
  }
}
