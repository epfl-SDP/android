package ch.epfl.sdp.mobile.ui.features.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.ui.features.ProfileColor
import ch.epfl.sdp.mobile.ui.features.social.*
import ch.epfl.sdp.mobile.ui.features.social.MatchResult.Reason.*
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.setContentWithLocalizedStrings
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

  @get:Rule val rule = createComposeRule()

  open class TestProfileState(override val matches: List<ChessMatch>) : ProfileState {
    override val email = "example@epfl.ch"
    override val pastGamesCount = 10
    override val puzzlesCount = 20
    override fun onSettingsClick() = Unit
    override fun onEditClick() = Unit
    override val backgroundColor = ProfileColor.Pink
    override val name = "Example"
    override val emoji = "🎁"
  }

  object FakeProfileState :
      TestProfileState(
          List(20) { ChessMatch("Konor($it)", Win(CHECKMATE), 27) },
      )

  @Test
  fun profile_isDisplayed() {
    rule.setContent { ProfileScreen(FakeProfileState) }

    rule.onNodeWithText(FakeProfileState.email).assertExists()
    rule.onNodeWithText(FakeProfileState.name).assertExists()
    rule.onNodeWithText(FakeProfileState.emoji).assertExists()
  }

  @Test
  fun counts_areDisplayed() {
    rule.setContent { ProfileScreen(FakeProfileState) }

    rule.onNodeWithText(FakeProfileState.pastGamesCount.toString()).assertExists()
    rule.onNodeWithText(FakeProfileState.puzzlesCount.toString()).assertExists()
  }

  @Test
  fun scroll_makesMatchesVisible() {
    rule.setContentWithLocalizedStrings { ProfileScreen(FakeProfileState) }

    rule.onRoot().performTouchInput { swipeUp() }
    rule.onAllNodesWithText("Konor", substring = true)
        .assertAny(SemanticsMatcher("exists") { true })
  }

  private fun testMatchResult(
      rule: ComposeContentTestRule,
      match: ChessMatch,
      expected: LocalizedStrings.() -> String,
  ) {
    val state = TestProfileState(List(20) { match })
    val strings = rule.setContentWithLocalizedStrings { ProfileScreen(state) }

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
