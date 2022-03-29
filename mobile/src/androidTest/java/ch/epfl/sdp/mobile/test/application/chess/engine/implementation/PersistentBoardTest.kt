package ch.epfl.sdp.mobile.test.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.implementation.PersistentPieceIdentifier
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PersistentBoardTest {

  private val pawn = Piece(Unit, Rank.Pawn, PersistentPieceIdentifier(0))

  @Test
  fun emptyBoard_hasNoPiece() {
    val board = emptyBoard<Unit>()
    for (i in 0 until Board.Size) {
      for (j in 0 until Board.Size) {
        assertThat(board[Position(i, j)]).isNull()
      }
    }
  }

  @Test
  fun buildBoard_addsPiece() {
    val position = Position(0, 0)
    val board = buildBoard<Piece<Unit>> { set(position, pawn) }
    assertThat(board[position]).isEqualTo(pawn)
  }

  @Test
  fun buildBoard_outOfBounds_addsNoPiece() {
    val position = Position(-1, -1)
    val board = buildBoard<Piece<Unit>> { set(position, pawn) }
    assertThat(board[position]).isNull()
  }

  @Test
  fun set_outOfBounds_isIgnored() {
    val position = Position(-1, -1)
    val board = emptyBoard<Piece<Unit>>().set(position, pawn)
    assertThat(board[position]).isNull()
  }
}