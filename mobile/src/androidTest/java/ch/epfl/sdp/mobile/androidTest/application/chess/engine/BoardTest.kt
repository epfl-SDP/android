package ch.epfl.sdp.mobile.androidTest.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.engine.toBoard
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BoardTest {

  @Test
  fun given_emptyBoard_when_flattening_then_remainsEqual() {
    val board = emptyBoard<Nothing>()
    assertThat(board.toBoard()).isEqualTo(board)
  }

  @Test
  fun given_nonEmptyBoard_when_flattening_then_remainsEqual() {
    val board =
        buildBoard<Unit> {
          set(Position(0, 0), Unit)
          set(Position(1, 1), Unit)
        }

    assertThat(board.toBoard()).isEqualTo(board)
  }
}
