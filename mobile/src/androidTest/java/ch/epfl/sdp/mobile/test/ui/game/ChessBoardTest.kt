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
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
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

  /**
   * An implementation of [ChessBoardState] which moves a single piece around the chessboard on drag
   * and drops.
   *
   * @param piece the [Piece] which should be moved.
   */
  class SinglePieceSnapshotChessBoardState(
      private val piece: Piece =
          object : Piece {
            override val color = White
            override val rank = Pawn
          },
  ) : ChessBoardState<Piece> {

    var position: Position by mutableStateOf(Position(0, 0))

    override val pieces: Map<Position, Piece>
      get() = mapOf(position to piece)

    override val availableMoves: Set<Position>
      get() = emptySet()

    override fun onDropPiece(
        piece: Piece,
        endPosition: Position,
    ) {
      position = endPosition
    }
  }

  /*
  @Test
  fun draggingPawnAroundIsSuccessful() = runTest {
    val state = SnapshotChessBoardState()
    val strings =
        rule.setContentWithLocalizedStrings {
          ChessBoard(state, Modifier.size(160.dp).testTag("board"))
        }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(90.dp.toPx(), 130.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), -40.dp.toPx()))
      up()
    }
    rule.awaitIdle()
    val inBounds =
        rule.onAllNodesWithContentDescription(
                strings.boardPieceContentDescription(
                    strings.boardColorWhite, strings.boardPiecePawn),
            )
            .fetchSemanticsNodes()
            .any { DpOffset(90.dp, 90.dp) in it.getBoundsInRoot() }
    assertThat(inBounds).isTrue()
  }
   */

  @Test
  fun draggingPawnOutsideBoard_works() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    rule.setContentWithLocalizedStrings {
      ChessBoard(state, Modifier.size(160.dp).testTag("board"))
    }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      moveBy(Offset(-20.dp.toPx(), -20.dp.toPx()))
      up()
    }
    assertThat(state.position).isEqualTo(Position(-1, -1))
  }

  @Test
  fun draggingPawnAround_withDisabledBoard_movesNothing() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings =
        rule.setContentWithLocalizedStrings {
          ChessBoard(state, Modifier.size(160.dp).testTag("board"), enabled = false)
        }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), 40.dp.toPx()))
      up()
    }
    val bounds =
        rule.onNodeWithContentDescription(
                strings.boardPieceContentDescription(
                    strings.boardColorWhite, strings.boardPiecePawn),
            )
            .getBoundsInRoot()
    assertThat(DpOffset(10.dp, 10.dp) in bounds).isTrue()
  }

  @Test
  fun draggingPawnAround_whileBoardIsEnabled_dropsOnRightTarget() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings =
        rule.setContentWithLocalizedStrings {
          ChessBoard(state, Modifier.size(160.dp).testTag("board"))
        }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), 20.dp.toPx()))
    }
    state.position = Position(1, 1)
    rule.onNodeWithTag("board").performTouchInput {
      moveBy(Offset(0.dp.toPx(), 20.dp.toPx())) // Still drop at (0, 3)
      up()
    }
    val bounds =
        rule.onNodeWithContentDescription(
                strings.boardPieceContentDescription(
                    strings.boardColorWhite, strings.boardPiecePawn),
            )
            .getBoundsInRoot()
    assertThat(DpOffset(10.dp, 50.dp) in bounds).isTrue()
  }

  @Test
  fun emptyDragGesture_doesNotMovePawn() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings =
        rule.setContentWithLocalizedStrings {
          ChessBoard(state, Modifier.size(160.dp).testTag("board"))
        }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      cancel() // Cancel the gesture, so we should not drop the pawn.
    }
    rule.awaitIdle()
    val bounds =
        rule.onNodeWithContentDescription(
                strings.boardPieceContentDescription(
                    strings.boardColorWhite, strings.boardPiecePawn),
            )
            .getBoundsInRoot()
    assertThat(DpOffset(10.dp, 10.dp) in bounds).isTrue()
  }

  @Test
  fun disablingBoardDuringDrag_dropsPiece() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    var enabled by mutableStateOf(true)
    val strings =
        rule.setContentWithLocalizedStrings {
          ChessBoard(state, Modifier.size(160.dp).testTag("board"), enabled = enabled)
        }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(10.dp.toPx(), 10.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), 20.dp.toPx()))
    }
    enabled = false // We expect the piece to be dropped mid-gesture.
    rule.onNodeWithTag("board").performTouchInput {
      moveBy(Offset(0.dp.toPx(), 20.dp.toPx()))
      up()
    }
    val bounds =
        rule.onNodeWithContentDescription(
                strings.boardPieceContentDescription(
                    strings.boardColorWhite, strings.boardPiecePawn),
            )
            .getBoundsInRoot()
    assertThat(DpOffset(10.dp, 30.dp) in bounds).isTrue()
  }
}
