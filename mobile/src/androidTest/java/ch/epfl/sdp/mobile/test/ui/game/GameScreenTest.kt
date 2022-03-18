package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import ch.epfl.sdp.mobile.state.ChessMove
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.Move
import org.junit.Rule
import org.junit.Test

class GameScreenTest {
  @get:Rule val rule = createComposeRule()

  private class SnapshotGameScreenState :
      GameScreenState<Piece>,
      ChessBoardState<Piece> by ChessBoardTest.SinglePieceSnapshotChessBoardState() {
    override val moves: List<Move>
      get() =
          listOf(
              ChessMove(1, "f3"),
              ChessMove(2, "e5"),
              ChessMove(3, "g4"),
              ChessMove(4, "Qh4#"),
          )
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
}
