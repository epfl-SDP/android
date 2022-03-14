package ch.epfl.sdp.mobile.test.application.chess

import ch.epfl.sdp.mobile.application.chess.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GameTest {

  @Test
  fun emptyGame_startsWithWhitePlayer() {
    val game = emptyGame()
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Color.White)
  }

  @Test
  fun emptyGame_validMove_switchesNextPlayer() {
    val game = emptyGame().play { Position(0, 6) += Delta(0, 1) }
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Color.Black)
  }

  @Test
  fun emptyGame_twoValidMoves_switchesFirstPlayer() {
    val game =
        emptyGame().play {
          Position(0, 6) += Delta(0, -1)
          Position(0, 1) += Delta(0, 1)
        }
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Color.White)
  }

  @Test
  fun emptyGame_kingsAreCorrectlyPlaced() {
    val game = emptyGame()
    val black = game.board[Position(4, 0)]
    val white = game.board[Position(4, 7)]
    assertThat(black).isEqualTo(Piece(Color.Black, Rank.King))
    assertThat(white).isEqualTo(Piece(Color.White, Rank.King))
  }
}