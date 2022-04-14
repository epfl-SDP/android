package ch.epfl.sdp.mobile.test.ui.profile

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.ChessMatchAdapter
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.profile.UserScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Tie
import org.junit.Rule
import org.junit.Test

class UserScreenTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_listOfMatches_when_loaded_then_display() {
    val strings =
        rule.setContentWithLocalizedStrings {
          UserScreen(
              header = { Text("Header") },
              profileTabBar = { Text("ProfileTabBar") },
              matches = listOf<ChessMatch>(ChessMatchAdapter("1", "adversary", Tie, 0)),
              onMatchClick = {},
              lazyColumnState = rememberLazyListState())
        }

    rule.onNodeWithText("Header").assertExists()
    rule.onNodeWithText(strings.profileMatchTitle("adversary")).assertExists()
  }
}
