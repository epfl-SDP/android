package ch.epfl.sdp.mobile.test.ui.play

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.play.PlayScreen
import ch.epfl.sdp.mobile.ui.play.PlayScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

open class TestPlayScreenState(
    val onNewGameClickAction: () -> Unit,
    val onGameItemClickAction: (ChessMatch) -> Unit,
    override val matches: List<ChessMatch>
) : PlayScreenState<ChessMatch> {
  override val user: AuthenticatedUser = mockk()
  override fun onLocalGameClick() {}
  override fun onOnlineGameClick() {}
  override fun onMatchClick(match: ChessMatch) {
    onGameItemClickAction(match)
  }
}

object FakePlayScreenState : TestPlayScreenState({}, {}, emptyList())

class PlayScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun newGame_isDisplayed() {
    val strings = rule.setContentWithLocalizedStrings { PlayScreen(FakePlayScreenState) }
    rule.onNodeWithText(strings.newGame).assertExists()
  }
}
