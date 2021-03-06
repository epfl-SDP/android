package ch.epfl.sdp.mobile.test.ui.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.state.ChessMatchAdapter
import ch.epfl.sdp.mobile.state.toColor
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import ch.epfl.sdp.mobile.ui.profile.ProfileScreen
import ch.epfl.sdp.mobile.ui.profile.VisitedProfileScreenState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Loss
import ch.epfl.sdp.mobile.ui.social.MatchResult.Reason.CHECKMATE
import ch.epfl.sdp.mobile.ui.social.MatchResult.Reason.FORFEIT
import ch.epfl.sdp.mobile.ui.social.Tie
import ch.epfl.sdp.mobile.ui.social.Win
import org.junit.Rule
import org.junit.Test

class VisitedProfileScreenTest {

  @get:Rule val rule = createComposeRule()

  open class TestProfileScreenState(
      override val matches: List<ChessMatch>,
      override val puzzles: List<PuzzleInfo>,
  ) : VisitedProfileScreenState<ChessMatch, PuzzleInfo> {
    override val pastGamesCount = 10
    override val solvedPuzzlesCount = 0
    override fun onChallengeClick() = Unit
    override val follows: Boolean = false
    override val backgroundColor = Color.Default.toColor()
    override val name = "Example"
    override val emoji = "🎁"
    override val followed = false
    override fun onMatchClick(match: ChessMatch) = Unit
    override fun onFollowClick() = Unit
    override fun onBack() = Unit
    override fun onPuzzleClick(puzzle: PuzzleInfo) = Unit
  }

  object FakeProfileScreenState :
      TestProfileScreenState(
          List(20) { ChessMatchAdapter("1", "Konor($it)", Win(CHECKMATE), 27) },
          emptyList(),
      )

  @Test
  fun profile_isDisplayed() {
    rule.setContent { ProfileScreen(FakeProfileScreenState) }

    rule.onNodeWithText(FakeProfileScreenState.name).assertExists()
    rule.onNodeWithText(FakeProfileScreenState.emoji).assertExists()
  }

  @Test
  fun counts_areDisplayed() {
    rule.setContent { ProfileScreen(FakeProfileScreenState) }

    rule.onNodeWithText(FakeProfileScreenState.pastGamesCount.toString()).assertExists()
  }

  @Test
  fun scroll_makesMatchesVisible() {
    rule.setContentWithLocalizedStrings { ProfileScreen(FakeProfileScreenState) }

    rule.onRoot().performTouchInput { swipeUp() }
    rule.onAllNodesWithText("Konor", substring = true)
        .assertAny(SemanticsMatcher("exists") { true })
  }

  private fun testMatchResult(
      rule: ComposeContentTestRule,
      match: ChessMatch,
      expected: LocalizedStrings.() -> String,
  ) {
    val state = TestProfileScreenState(List(20) { match }, emptyList())
    val strings = rule.setContentWithLocalizedStrings { ProfileScreen(state) }

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

  @Test
  fun given_socialScreen_when_userProfileVisited_then_backButtonExists() {
    val strings =
        rule.setContentWithLocalizedStrings { ProfileScreen(state = FakeProfileScreenState) }
    rule.onNodeWithContentDescription(strings.socialCloseVisitedProfile).assertExists()
  }
}
