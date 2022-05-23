package ch.epfl.sdp.mobile.test.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.engine.PieceIdentifier
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank.King
import ch.epfl.sdp.mobile.application.chess.engine.implementation.MutableBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.MutableBoardPiece
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MutableBoardTest {

  @Test
  fun given_boardWithOnePiece_when_countsPieces_then_returnsOne() {
    val board =
        MutableBoard().apply {
          set(Position(0, 0), MutableBoardPiece(PieceIdentifier(0), King, White))
        }
    var count = 0
    board.forEachPiece { _, _ -> count++ }

    assertThat(count).isEqualTo(1)
  }

  @Test
  fun given_emptyBoard_when_callsContains_then_returnsFalse() {
    val board = MutableBoard()
    assertThat(board.contains(Position(0, 0))).isFalse()
  }

  @Test
  fun given_board_when_setsOutOfBounds_then_doesNothing() {
    val board = MutableBoard()
    val piece = MutableBoardPiece(PieceIdentifier(0), King, White)
    board[Position(-1, -1)] = piece
    Position.all().forEach { assertThat(board.contains(it)).isFalse() }
  }
}
