package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.runtime.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.MovesInfoState.Move
import ch.epfl.sdp.mobile.ui.game.SpeechRecognizerState.SpeechRecognizerError
import ch.epfl.sdp.mobile.ui.game.SpeechRecognizerState.SpeechRecognizerError.*
import org.junit.Rule
import org.junit.Test

class GameScreenTest {
  @get:Rule val rule = createComposeRule()

  private class SnapshotGameScreenState :
      GameScreenState<Piece>,
      MovableChessBoardState<Piece> by ChessBoardTest.SinglePieceSnapshotChessBoardState() {

    override var currentError: SpeechRecognizerError = None
    override val textToSpeechEnabled = false
    override fun onTextToSpeechToggle() {}

    override val moves: List<Move>
      get() =
          listOf(
              Move("f3"),
              Move("e5"),
              Move("g4"),
              Move("Qh4#"),
          )
    override val white = PlayersInfoState.Player("Alex", PlayersInfoState.Message.None)
    override val black = PlayersInfoState.Player("Matt", PlayersInfoState.Message.None)

    override fun onArClick() = Unit

    override var listening by mutableStateOf(false)
      private set

    override fun onListenClick() {
      listening = !listening
    }

    override fun onBackClick() = Unit
  }

  @Composable
  private fun rememberSnapshotGameScreenState(): SnapshotGameScreenState {
    return remember { SnapshotGameScreenState() }
  }

  @Test
  fun moveList_isDisplayed() {
    rule.setContent { GameScreen(state = rememberSnapshotGameScreenState()) }

    rule.onNodeWithText("1. f3").assertExists()
    rule.onNodeWithText("2. e5").assertExists()
    rule.onNodeWithText("3. g4").assertExists()
    rule.onNodeWithText("4. Qh4#").assertExists()
  }

  @Test
  fun aWhitePawn_isDisplayed() {
    val strings =
        rule.setContentWithLocalizedStrings {
          GameScreen(state = rememberSnapshotGameScreenState())
        }

    rule.onNodeWithContentDescription(
            strings.boardPieceContentDescription(strings.boardColorWhite, strings.boardPiecePawn),
        )
        .assertExists()
  }

  @Test
  fun clickingListening_togglesListeningMode() {
    val strings =
        rule.setContentWithLocalizedStrings { GameScreen(rememberSnapshotGameScreenState()) }

    rule.onNodeWithContentDescription(strings.gameMicOffContentDescription).performClick()
    rule.onNodeWithContentDescription(strings.gameMicOnContentDescription).assertExists()
  }
}
