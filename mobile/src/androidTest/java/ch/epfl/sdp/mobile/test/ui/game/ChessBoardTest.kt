package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.ChessBoard
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Position
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.Pawn
import ch.epfl.sdp.mobile.ui.game.MovableChessBoardState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChessBoardTest {

  @get:Rule val rule = createComposeRule()

  /**
   * An implementation of [MovableChessBoardState] which moves a single piece around the chessboard
   * on drag and drops.
   *
   * @param piece the [Piece] which should be moved.
   */
  class SinglePieceSnapshotChessBoardState(
      private val piece: Piece =
          object : Piece {
            override val color = White
            override val rank = Pawn
          },
  ) : MovableChessBoardState<Piece> {

    var position: Position by mutableStateOf(Position(0, 0))

    override val selectedPosition: Position? = null
    override val checkPosition: Position? = null

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

    override fun onPositionClick(position: Position) = Unit
  }

  @Test
  fun draggingPawnAroundIsSuccessful() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings = rule.setContentWithLocalizedStrings { ChessBoard(state) }
    val robot = ChessBoardRobot(rule, strings)
    robot.performInput {
      down(0, 0)
      moveBy(0, 2)
      up()
    }
    rule.awaitIdle()
    robot.assertHasPiece(0, 2, White, Pawn)
  }

  @Test
  fun draggingPawnOutsideBoard_works() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings = rule.setContentWithLocalizedStrings { ChessBoard(state) }
    val robot = ChessBoardRobot(rule, strings)
    robot.performInput {
      down(0, 0)
      moveBy(-1, -1)
      up()
    }
    assertThat(state.position).isEqualTo(Position(-1, -1))
  }

  @Test
  fun draggingPawnAround_withDisabledBoard_movesNothing() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings = rule.setContentWithLocalizedStrings { ChessBoard(state, enabled = false) }
    val robot = ChessBoardRobot(rule, strings)
    robot.performInput {
      down(0, 0)
      moveBy(0, 2)
      up()
    }
    robot.assertHasPiece(0, 0, White, Pawn)
  }

  @Test
  fun draggingPawnAround_whileBoardIsEnabled_dropsOnRightTarget() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings = rule.setContentWithLocalizedStrings { ChessBoard(state) }
    val robot = ChessBoardRobot(rule, strings)
    robot.performInput {
      down(0, 0)
      moveBy(0, 1)
    }
    state.position = Position(1, 1)
    robot.performInput {
      moveBy(0, 1) // Still drop at (0, 2)
      up()
    }
    robot.assertHasPiece(0, 2, White, Pawn)
  }

  @Test
  fun emptyDragGesture_doesNotMovePawn() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    val strings = rule.setContentWithLocalizedStrings { ChessBoard(state) }
    val robot = ChessBoardRobot(rule, strings)
    robot.performInput {
      down(0, 0)
      up()
    }
    robot.assertHasPiece(0, 0, White, Pawn)
  }

  @Test
  fun disablingBoardDuringDrag_dropsPiece() = runTest {
    val state = SinglePieceSnapshotChessBoardState()
    var enabled by mutableStateOf(true)
    val strings = rule.setContentWithLocalizedStrings { ChessBoard(state, enabled = enabled) }
    val robot = ChessBoardRobot(rule, strings)
    robot.performInput {
      down(0, 0)
      moveBy(0, 1)
    }
    enabled = false // We expect the piece to be dropped mid-gesture.
    robot.performInput {
      moveBy(0, 1)
      up()
    }
    robot.assertHasPiece(0, 1, White, Pawn)
  }
}
