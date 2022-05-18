package ch.epfl.sdp.mobile.test.ui.game.ar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.test.assertThrows
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import dev.romainguy.kotlin.math.Float3
import java.lang.IllegalStateException
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {

  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  @Test
  fun given_emptyState_when_initChessScene_then_rootIsNotNull() {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(TestScope())
    chessScene.context = rule.activity.applicationContext

    assertNotEquals(null, chessScene.boardNode)
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(TestScope())
    chessScene.context = rule.activity.applicationContext

    chessScene.scale(4f)
    assertEquals(Float3(4f), chessScene.boardNode.scale)
  }

  @Test
  fun given_chessSceneWithoutContext_when_callUpdate_then_throwIllegalStateException() = runTest {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(this)

    assertThrows<IllegalStateException> { chessScene.loadBoard(emptyMap()) }
  }
}
