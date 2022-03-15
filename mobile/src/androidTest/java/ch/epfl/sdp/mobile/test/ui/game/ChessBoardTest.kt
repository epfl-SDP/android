package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.FakeChessBoardState
import ch.epfl.sdp.mobile.ui.game.ChessBoard
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ChessBoardTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun test() = runTest {
    val state = FakeChessBoardState()
    rule.setContent { ChessBoard(state, Modifier.size(160.dp).testTag("board")) }
    rule.onNodeWithTag("board").performTouchInput {
      down(Offset(90.dp.toPx(), 130.dp.toPx()))
      moveBy(Offset(0.dp.toPx(), -40.dp.toPx()))
      up()
    }
    rule.awaitIdle()
    val inBounds =
        rule.onAllNodesWithContentDescription("White Pawn").fetchSemanticsNodes().any {
          DpOffset(90.dp, 90.dp) in it.getBoundsInRoot()
        }
    assertThat(inBounds).isTrue()
  }
}

private fun SemanticsNode.getBoundsInRoot(): DpRect =
    with(root!!.density) {
      boundsInRoot.let { DpRect(it.left.toDp(), it.top.toDp(), it.right.toDp(), it.bottom.toDp()) }
    }

private operator fun DpRect.contains(offset: DpOffset): Boolean {
  return offset.x in left..right && offset.y in top..bottom
}
