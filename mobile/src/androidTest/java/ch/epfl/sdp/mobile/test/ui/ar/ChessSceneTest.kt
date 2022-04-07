package ch.epfl.sdp.mobile.test.ui.ar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.lifecycleScope
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.ui.ar.ChessScene
import dev.romainguy.kotlin.math.Float3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {
  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  @Test
  fun given_emptyState_when_initChessScene_Then_rootIsNotNull() {
    val board = Game.create()
    val chessScene =
        ChessScene(rule.activity.applicationContext, rule.activity.lifecycleScope, board.board)

    assertNotEquals(null, chessScene.boardNode)
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() {
    val board = Game.create()
    val chessScene =
        ChessScene(rule.activity.applicationContext, rule.activity.lifecycleScope, board.board)

    chessScene.scale(4f)
    assertEquals(Float3(4f), chessScene.boardNode.scale)
  }
}
