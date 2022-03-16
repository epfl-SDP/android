package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.FakeChessBoardState
import ch.epfl.sdp.mobile.test.ui.contains
import ch.epfl.sdp.mobile.test.ui.getBoundsInRoot
import ch.epfl.sdp.mobile.ui.game.ChessBoard
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.Pawn
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChessBoardTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun draggingPawnAroundIsSuccessful() = runTest {
    val state = FakeChessBoardState()
    rule.setContent { ChessBoard(state, Modifier.size(160.dp).testTag("board")) }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(90.dp.toPx(), 130.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), -40.dp.toPx()))
      up()
    }
    rule.awaitIdle()
    val inBounds =
        rule.onAllNodesWithContentDescription("White Pawn").fetchSemanticsNodes().any {
          DpOffset(90.dp, 90.dp) in it.getBoundsInRoot()
        }
    assertThat(inBounds).isTrue()
  }

  /**
   * An implementation of [ChessBoardState] which moves a single piece around the chessboard on drag
   * and drops.
   *
   * @param piece the [Piece] which should be moved.
   */
  class SinglePieceSnapshotChessBoardState(
      private val piece: Piece<Unit> = Piece(Unit, Pawn, White),
  ) : ChessBoardState<Unit> {

    var position: Position by mutableStateOf(Position(0, 0))

    override val pieces: Map<Position, Piece<Unit>>
      get() = mapOf(position to piece)

    override fun onDropPiece(
        piece: Piece<Unit>,
        endPosition: Position,
    ) {
      position = endPosition
    }
  }

  @Test
  fun draggingPawnAround_whileBoardIsSuccessful_dropsOnRightTarget() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    rule.setContent { ChessBoard(state, Modifier.size(160.dp).testTag("board")) }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), 20.dp.toPx()))
    }
    state.position = Position(1, 1)
    rule.onNodeWithTag("board").performTouchInput {
      moveBy(Offset(0.dp.toPx(), 20.dp.toPx())) // Still drop at (0, 3)
      up()
    }
    val bounds = rule.onNodeWithContentDescription("White Pawn").getBoundsInRoot()
    assertThat(DpOffset(10.dp, 50.dp) in bounds).isTrue()
  }

  @Test
  fun emptyDragGesture_doesNotMovePawn() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    rule.setContent { ChessBoard(state, Modifier.size(160.dp).testTag("board")) }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      cancel() // Cancel the gesture, so we should not drop the pawn.
    }
    rule.awaitIdle()
    val bounds = rule.onNodeWithContentDescription("White Pawn").getBoundsInRoot()
    assertThat(DpOffset(10.dp, 10.dp) in bounds).isTrue()
  }
}
