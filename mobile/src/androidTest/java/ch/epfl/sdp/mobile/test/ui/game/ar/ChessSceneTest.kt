package ch.epfl.sdp.mobile.test.ui.game.ar

import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import dev.romainguy.kotlin.math.Float3
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ChessSceneTest {

  @Test
  fun given_emptyState_when_initChessScene_then_rootIsNotNull() {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(TestScope())

    assertNotEquals(null, chessScene.boardNode)
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(TestScope())

    chessScene.scale(4f)
    assertEquals(Float3(4f), chessScene.boardNode.scale)
  }
}
