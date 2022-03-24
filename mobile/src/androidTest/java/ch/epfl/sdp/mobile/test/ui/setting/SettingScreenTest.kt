package ch.epfl.sdp.mobile.test.ui.setting

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Loss
import ch.epfl.sdp.mobile.ui.social.MatchResult.Reason.*
import ch.epfl.sdp.mobile.ui.social.Tie
import ch.epfl.sdp.mobile.ui.social.Win
import org.junit.Rule
import org.junit.Test

class SettingScreenTest {

  @get:Rule val rule = createComposeRule()

  open class TestSettingScreenState(override val matches: List<ChessMatch>) : SettingScreenState {
    override val email = "example@epfl.ch"
    override val pastGamesCount = 10
    override val puzzlesCount = 12

    override fun onEditClick() = Unit
    override fun onSettingsClick() = Unit
    override val backgroundColor = Color.Default
    override val name = "Example"
    override val emoji = "🎁"
  }

  object FakeSetttingScreenState :
      TestSettingScreenState(
          List(20) { ChessMatch("Konor($it)", Win(CHECKMATE), 27) },
      ) {
    override val puzzlesCount = 12
  }

  @Test
  fun profile_isDisplayed() {
    rule.setContent { SettingsScreen(FakeSetttingScreenState) }

    rule.onNodeWithText(FakeSetttingScreenState.email).assertExists()
    rule.onNodeWithText(FakeSetttingScreenState.name).assertExists()
    rule.onNodeWithText(FakeSetttingScreenState.emoji).assertExists()
  }

  @Test
  fun pastGameCount_areDisplayed() {
    rule.setContent { SettingsScreen(FakeSetttingScreenState) }

    rule.onNodeWithText(FakeSetttingScreenState.pastGamesCount.toString()).assertExists()
  }

  @Test
  fun scroll_makesMatchesVisible() {
    rule.setContentWithLocalizedStrings { SettingsScreen(FakeSetttingScreenState) }

    rule.onRoot().performTouchInput { swipeUp() }
    rule.onAllNodesWithText("Konor", substring = true)
        .assertAny(SemanticsMatcher("exists") { true })
  }

  private fun testMatchResult(
      rule: ComposeContentTestRule,
      match: ChessMatch,
      expected: LocalizedStrings.() -> String,
  ) {
    val state = TestSettingScreenState(List(20) { match })
    val strings = rule.setContentWithLocalizedStrings { SettingsScreen(state) }

    rule.onRoot().performTouchInput { swipeUp() }
    rule.onAllNodesWithText(expected(strings)).assertAny(SemanticsMatcher("exists") { true })
  }

  @Test
  fun tieMatchResult_isDisplayed() {
    testMatchResult(rule, ChessMatch("John", Tie, 10)) { profileTieInfo(10) }
  }

  @Test
  fun lossByCheckmateResult_isDisplayed() {
    testMatchResult(rule, ChessMatch("John", Loss(CHECKMATE), 10)) { profileLostByCheckmate(10) }
  }

  @Test
  fun lossByForfeitResult_isDisplayed() {
    testMatchResult(rule, ChessMatch("John", Loss(FORFEIT), 10)) { profileLostByForfeit(10) }
  }

  @Test
  fun winCheckmateResult_isDisplayed() {
    testMatchResult(rule, ChessMatch("John", Win(CHECKMATE), 10)) { profileWonByCheckmate(10) }
  }

  @Test
  fun winByForfeitResult_isDisplayed() {
    testMatchResult(rule, ChessMatch("John", Win(FORFEIT), 10)) { profileWonByForfeit(10) }
  }
}