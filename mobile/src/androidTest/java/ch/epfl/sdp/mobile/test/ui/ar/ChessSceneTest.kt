package ch.epfl.sdp.mobile.test.ui.ar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.lifecycleScope
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import dev.romainguy.kotlin.math.Float3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {
  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  class SinglePieceSnapshotChessBoardState(
      private val piece: ChessBoardState.Piece =
          object : ChessBoardState.Piece {
            override val color = ChessBoardState.Color.White
            override val rank = ChessBoardState.Rank.Pawn
          },
  ) : ChessBoardState<ChessBoardState.Piece> {
    var position: ChessBoardState.Position by mutableStateOf(ChessBoardState.Position(0, 0))

    override val pieces: Map<ChessBoardState.Position, ChessBoardState.Piece>
      get() = mapOf(position to piece)
    override val checkPosition: ChessBoardState.Position? = null
  }

  @Test
  fun given_emptyState_when_initChessScene_then_rootIsNotNull() {
    val state = SinglePieceSnapshotChessBoardState()
    val chessScene =
        ChessScene(rule.activity.applicationContext, rule.activity.lifecycleScope, state.pieces)

    assertNotEquals(null, chessScene.boardNode)
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() {
    val state = SinglePieceSnapshotChessBoardState()
    val chessScene =
        ChessScene(rule.activity.applicationContext, rule.activity.lifecycleScope, state.pieces)

    chessScene.scale(4f)
    assertEquals(Float3(4f), chessScene.boardNode.scale)
  }
}
