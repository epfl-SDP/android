package ch.epfl.sdp.mobile.test.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.Color.Black
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GameTest {

  @Test
  fun emptyGame_startsWithWhitePlayer() {
    val game = Game.create()
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Color.White)
  }

  @Test
  fun emptyGame_validMove_switchesNextPlayer() {
    val game = Game.create().play { Position(0, 6) += Delta(0, -1) }
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Black)
  }

  @Test
  fun emptyGame_outOfBoundsMove_preservesPlayer() {
    val game = Game.create().play { Position(0, 0) += Delta(0, -1) }
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Color.White)
  }

  @Test
  fun emptyGame_twoValidMoves_switchesFirstPlayer() {
    val game =
        Game.create().play {
          Position(0, 6) += Delta(0, -1)
          Position(0, 1) += Delta(0, 1)
        }
    val step = game.nextStep as NextStep.MovePiece
    assertThat(step.turn).isEqualTo(Color.White)
  }

  @Test
  fun emptyGame_kingsAreCorrectlyPlaced() {
    val game = Game.create()
    val black = game.board[Position(4, 0)]
    val white = game.board[Position(4, 7)]
    assertThat(black?.color).isEqualTo(Black)
    assertThat(black?.rank).isEqualTo(Rank.King)
    assertThat(white?.color).isEqualTo(Color.White)
    assertThat(white?.rank).isEqualTo(Rank.King)
  }

  @Test
  fun emptyGame_twoPawnsAreDifferent() {
    val game = Game.create()
    val a = game.board[Position(0, 1)]
    val b = game.board[Position(1, 1)]
    assertThat(a).isNotEqualTo(b)
  }

  @Test
  fun game_canPerformCastling() {
    val game =
        Game.create().play {
          Position(4, 6) += Delta(0, -2) // White moves pawn
          Position(1, 0) += Delta(1, 2) // Black moves knight
          Position(5, 7) += Delta(-1, -1) // White moves bishop
          Position(2, 2) += Delta(-1, -2) // Black moves knight
          Position(6, 7) += Delta(-1, -2) // White moves knight
          Position(1, 0) += Delta(1, 2) // Black moves knight
          Position(4, 7) += Delta(2, 0) // White castles !
        }
    assertThat(game.board[Position(5, 7)]?.rank).isEqualTo(Rank.Rook)
    assertThat(game.board[Position(6, 7)]?.rank).isEqualTo(Rank.King)
  }

  @Test
  fun gameHistory_isDenormalized() {
    val game =
        Game.create().play {
          Position(6, 7) += Delta(-1, -2)
          Position(6, 0) += Delta(-1, 2)
        }

    val second = game.previous
    val first = game.previous?.first?.previous

    assertThat(first?.second).isEqualTo(Action(Position(6, 7), Delta(-1, -2)))
    assertThat(second?.second).isEqualTo(Action(Position(6, 0), Delta(-1, 2)))
  }

  @Test
  fun foolsMate_isMate() {
    val game = Game.create().play { FoolsMate() }
    assertThat(game.nextStep).isEqualTo(NextStep.Checkmate(winner = Black))
  }
}
