package ch.epfl.sdp.mobile.test.ui.game.ar

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sdp.mobile.application.chess.engine.Color.*
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Piece as EnginePiece
import ch.epfl.sdp.mobile.application.chess.engine.PieceIdentifier
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import com.google.common.truth.Truth.assertThat
import io.github.sceneview.math.Scale
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {

  @get:Rule val rule = ActivityScenarioRule(ComponentActivity::class.java)

  private val simpleBoard =
      Game.create().board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  @Test
  fun given_emptyState_when_initChessScene_then_has32ChildrenNodes() = runTest {
    rule.scenario.onActivity {
      val chessScene = ChessScene(it.applicationContext, it.lifecycleScope, simpleBoard)

      launch {
        yieldWhile { chessScene.boardNode.children.size != simpleBoard.size }
        assertThat(chessScene.boardNode.children.size).isEqualTo(simpleBoard.size)
      }
    }
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() = runTest {
    rule.scenario.onActivity {
      val chessScene = ChessScene(it.applicationContext, it.lifecycleScope, simpleBoard)

      launch {
        yieldWhile { chessScene.boardNode.children.size != simpleBoard.size }

        chessScene.scale(4f)
        assertThat(chessScene.boardNode.scale).isEqualTo(Scale(4f))
      }
    }
  }

  @Test
  fun given_chessScene_whenUpdated_then_haveCorrectNumberOfChildren() = runTest {
    rule.scenario.onActivity {
      val chessScene = ChessScene(it.applicationContext, it.lifecycleScope, simpleBoard)

      val iterator = simpleBoard.entries.iterator()
      val oldMovePiece = iterator.next()
      val newPosition = Position(oldMovePiece.key.x + 1, oldMovePiece.key.y)
      val newMovePiece = Pair(newPosition, oldMovePiece.value)

      val newPiece = (Position(4, 5) to Piece(EnginePiece(White, Rank.Pawn, PieceIdentifier(40))))
      val newBoard = mapOf(newMovePiece, newPiece)

      launch {
        yieldWhile { chessScene.boardNode.children.size != simpleBoard.size }

        chessScene.update(newBoard)
      }

      launch {
        yieldWhile { chessScene.boardNode.children.size != newBoard.size }

        assertThat(chessScene.boardNode.children.size).isEqualTo(newBoard.size)
      }
    }
  }
}

/**
 * This function yields until the [predicate] is not satisfied.
 *
 * @param predicate if this function returns true yield, otherwise the function ends.
 */
private suspend fun yieldWhile(predicate: () -> Boolean) {
  while (predicate()) yield()
}
