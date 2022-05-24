package ch.epfl.sdp.mobile.test.ui.game.ar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.lifecycleScope
import ch.epfl.sdp.mobile.application.chess.engine.Color.*
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Piece as EnginePiece
import ch.epfl.sdp.mobile.application.chess.engine.PieceIdentifier
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import com.google.common.truth.Truth.assertThat
import io.github.sceneview.math.Scale
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {

  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  private val simpleBoard =
      Game.create().board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  @Test
  fun given_emptyState_when_initChessScene_then_has32ChildrenNodes() = runTest {
    val context = rule.activity.applicationContext

    val chessScene = ChessScene(context, rule.activity.lifecycleScope, simpleBoard)

    rule.waitUntil(5000) { chessScene.boardNode.children.size == simpleBoard.size }

    assertThat(chessScene.boardNode.children.size).isEqualTo(simpleBoard.size)
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() = runTest {
    val context = rule.activity.applicationContext

    val chessScene = ChessScene(context, rule.activity.lifecycleScope, simpleBoard)

    rule.waitUntil(5000) { chessScene.boardNode.children.size == simpleBoard.size }

    chessScene.scale(4f)
    assertThat(chessScene.boardNode.scale).isEqualTo(Scale(4f))
  }

  @Test
  fun given_chessScene_whenUpdated_then_haveCorrectNumberOfChildren() {
    val context = rule.activity.applicationContext

    val chessScene = ChessScene(context, rule.activity.lifecycleScope, simpleBoard)
    rule.waitUntil(5000) { chessScene.boardNode.children.size == simpleBoard.size }

    val iterator = simpleBoard.entries.iterator()
    val oldMovePiece = iterator.next()
    val newMovePiece = oldMovePiece.apply { (Position(4, 4) to value) }.toPair()

    val newPiece = (Position(4, 5) to Piece(EnginePiece(White, Rank.Pawn, PieceIdentifier(40))))

    val newBoard = mapOf(newMovePiece, newPiece)

    chessScene.update(newBoard)

    rule.waitUntil(5000) { chessScene.boardNode.children.size == newBoard.size }

    assertThat(chessScene.boardNode.children.size).isEqualTo(newBoard.size)
  }
}
