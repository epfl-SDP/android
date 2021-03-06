package ch.epfl.sdp.mobile.test.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.Color.White
import ch.epfl.sdp.mobile.application.chess.engine.Rank.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CheckmateTest {

  private val whiteKing = Piece(White, King, PieceIdentifier(0))
  private val whiteRook1 = Piece(White, Rook, PieceIdentifier(1))
  private val whiteRook2 = Piece(White, Rook, PieceIdentifier(2))
  private val whitePawn = Piece(White, Pawn, PieceIdentifier(0))
  private val blackKing = Piece(Black, King, PieceIdentifier(0))

  @Test
  fun twoRooks_canPutAdversaryKing_inCheckmate() {
    val game =
        buildGame(Black) {
          set(Position(0, 0), whiteRook1)
          set(Position(0, 1), whiteRook2)
          set(Position(3, 0), blackKing)
          set(Position(7, 0), whiteKing)
        }
    assertThat(game.nextStep).isEqualTo(NextStep.Checkmate(winner = White))
  }

  @Test
  fun pawnAndKing_canPutAdversaryKing_inStalemate() {
    val game =
        buildGame(Black) {
          set(Position(1, 0), blackKing)
          set(Position(1, 1), whitePawn)
          set(Position(1, 2), whiteKing)
        }
    assertThat(game.nextStep).isEqualTo(NextStep.Stalemate)
  }
}
