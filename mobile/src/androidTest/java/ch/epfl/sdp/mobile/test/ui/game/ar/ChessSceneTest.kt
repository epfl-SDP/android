package ch.epfl.sdp.mobile.test.ui.game.ar

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ch.epfl.sdp.mobile.application.chess.engine.Color.*
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.state.HomeActivity
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ar.ChessScene
import com.google.common.truth.Truth.assertThat
import dev.romainguy.kotlin.math.Float3
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChessSceneTest {

  @get:Rule val rule = createAndroidComposeRule<HomeActivity>()

  private val simpleBoard =
      Game.create().board.associate { (pos, piece) -> pos.toPosition() to Piece(piece) }

  @Test
  fun given_emptyState_when_initChessScene_then_rootIsNotNull() = runTest {
    val context = mockk<Context>()

    val chessScene = ChessScene(context, this, simpleBoard)

    assertThat(chessScene.boardNode).isNotNull()
  }

  @Test
  fun given_chessScene_when_scaled_then_rootHasCorrectScaleVector() = runTest {
    val context = mockk<Context>()

    val chessScene = ChessScene(context, this, simpleBoard)

    chessScene.scale(4f)
    assertThat(chessScene.boardNode.scale).isEqualTo(Float3(4f))
  }
}
