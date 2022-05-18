package ch.epfl.sdp.mobile.test.ui.game.ar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState
import ch.epfl.sdp.mobile.test.assertThrows
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import com.google.common.truth.Truth.assertThat
import dev.romainguy.kotlin.math.Float3
import java.lang.IllegalStateException
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {

  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  @Test
  fun given_emptyState_when_initChessScene_then_rootIsNotNull() = runTest {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(this)
    chessScene.context = rule.activity.applicationContext

    assertThat(chessScene.boardNode).isNotNull()
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() = runTest {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(this)
    chessScene.context = rule.activity.applicationContext

    chessScene.scale(4f)
    assertThat(chessScene.boardNode.scale).isEqualTo(Float3(4f))
  }

  @Test
  fun given_chessSceneWithoutContext_when_callUpdate_then_throwIllegalStateException() = runTest {
    val chessScene = ChessScene<DelegatingChessBoardState.Piece>(this)

    assertThrows<IllegalStateException> { chessScene.loadBoard(emptyMap()) }
  }
}
