package ch.epfl.sdp.mobile.test.ui.setting

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.state.ChessMatchAdapter
import ch.epfl.sdp.mobile.state.toColor
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

  open class TestSettingScreenState(override val matches: List<ChessMatch>) :
      SettingScreenState<ChessMatch> {
    override val email = "example@epfl.ch"
    override val pastGamesCount = 10
    override fun onMatchClick(match: ChessMatch) = Unit

    override val puzzlesCount = 12

    override val backgroundColor = Color.Default.toColor()
    override fun onEditProfileNameClick() = Unit
    override fun onEditProfileImageClick() = Unit
    override val name = "Example"
    override val emoji = "🎁"
    override val followed = true
    override fun onBack() = Unit
  }

  object FakeSettingScreenState :
      TestSettingScreenState(
          List(20) { ChessMatchAdapter("1", "Konor($it)", Win(CHECKMATE), 27) },
      ) {
    override val puzzlesCount = 12
  }

  @Test
  fun profile_isDisplayed() {
    rule.setContent { SettingsScreen(FakeSettingScreenState) }

    rule.onNodeWithText(FakeSettingScreenState.email).assertExists()
    rule.onNodeWithText(FakeSettingScreenState.name).assertExists()
    rule.onNodeWithText(FakeSettingScreenState.emoji).assertExists()
  }

  @Test
  fun pastGameCount_areDisplayed() {
    rule.setContent { SettingsScreen(FakeSettingScreenState) }

    rule.onNodeWithText(FakeSettingScreenState.pastGamesCount.toString()).assertExists()
  }

  @Test
  fun scroll_makesMatchesVisible() {
    rule.setContentWithLocalizedStrings { SettingsScreen(FakeSettingScreenState) }

    rule.onRoot().performTouchInput { swipeUp() }
    rule.onAllNodesWithText("Konor", substring = true)
        .assertAny(SemanticsMatcher("exists") { true })
  }

  private fun testMatchResult(
      rule: ComposeContentTestRule,
      match: ChessMatchAdapter,
      expected: LocalizedStrings.() -> String,
  ) {
    val state = TestSettingScreenState(List(20) { match })
    val strings = rule.setContentWithLocalizedStrings { SettingsScreen(state) }

    rule.onRoot().performTouchInput { swipeUp() }
    rule.onAllNodesWithText(expected(strings)).assertAny(SemanticsMatcher("exists") { true })
  }

  @Test
  fun tieMatchResult_isDisplayed() {
    testMatchResult(rule, ChessMatchAdapter("1", "John", Tie, 10)) { profileTieInfo(10) }
  }

  @Test
  fun lossByCheckmateResult_isDisplayed() {
    testMatchResult(rule, ChessMatchAdapter("1", "John", Loss(CHECKMATE), 10)) {
      profileLostByCheckmate(10)
    }
  }

  @Test
  fun lossByForfeitResult_isDisplayed() {
    testMatchResult(rule, ChessMatchAdapter("1", "John", Loss(FORFEIT), 10)) {
      profileLostByForfeit(10)
    }
  }

  @Test
  fun winCheckmateResult_isDisplayed() {
    testMatchResult(rule, ChessMatchAdapter("1", "John", Win(CHECKMATE), 10)) {
      profileWonByCheckmate(10)
    }
  }

  @Test
  fun winByForfeitResult_isDisplayed() {
    testMatchResult(rule, ChessMatchAdapter("1", "John", Win(FORFEIT), 10)) {
      profileWonByForfeit(10)
    }
  }
}
