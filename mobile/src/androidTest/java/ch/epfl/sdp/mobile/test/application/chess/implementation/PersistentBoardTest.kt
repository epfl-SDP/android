package ch.epfl.sdp.mobile.test.application.chess.implementation

import ch.epfl.sdp.mobile.application.chess.Board
import ch.epfl.sdp.mobile.application.chess.Piece
import ch.epfl.sdp.mobile.application.chess.Position
import ch.epfl.sdp.mobile.application.chess.Rank
import ch.epfl.sdp.mobile.application.chess.implementation.PersistentPieceIdentifier
import ch.epfl.sdp.mobile.application.chess.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.implementation.emptyBoard
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
