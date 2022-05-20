package ch.epfl.sdp.mobile.test.application.chess.engine2

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.implementation.buildBoard
import ch.epfl.sdp.mobile.application.chess.engine.implementation.emptyBoard
import ch.epfl.sdp.mobile.application.chess.engine2.toBoard
import ch.epfl.sdp.mobile.application.chess.engine2.toMutableBoard
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Test {

  @Test
  fun something() {
    val emptyBoard = emptyBoard<Piece<Color>>()
    val other = emptyBoard.toMutableBoard().toBoard()

    assertThat(other).isEmpty()
  }

  @Test
  fun startConfiguration() {
    val startBoard =
        buildBoard<Piece<Color>> {

          /** Populates a [row] with all the pieces of a given [color]. */
          fun populateSide(row: Int, color: Color) {
            set(Position(0, row), Piece(color, Rank.Rook, PieceIdentifier(0)))
            set(Position(1, row), Piece(color, Rank.Knight, PieceIdentifier(0)))
            set(Position(2, row), Piece(color, Rank.Bishop, PieceIdentifier(0)))
            set(Position(3, row), Piece(color, Rank.Queen, PieceIdentifier(0)))
            set(Position(4, row), Piece(color, Rank.King, PieceIdentifier(0)))
            set(Position(5, row), Piece(color, Rank.Bishop, PieceIdentifier(1)))
            set(Position(6, row), Piece(color, Rank.Knight, PieceIdentifier(1)))
            set(Position(7, row), Piece(color, Rank.Rook, PieceIdentifier(1)))
          }

          // Populate the pieces.
          populateSide(0, Color.Black)
          populateSide(7, Color.White)

          // Populate the pawns.
          var id = PieceIdentifier(0)
          repeat(Board.Size) { column ->
            set(Position(column, 1), Piece(Color.Black, Rank.Pawn, id))
            set(Position(column, 6), Piece(Color.White, Rank.Pawn, id))
            id++
          }
        }
    val other = startBoard.toMutableBoard().toBoard()
    assertThat(other).isEqualTo(startBoard)
  }
}
