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
    var game = emptyGame()
    val step1 = game.nextStep as NextStep.MovePiece
    game = step1.move(Position(0, 6), Delta(0, 1))
    val step2 = game.nextStep as NextStep.MovePiece
    assertThat(step2.turn).isEqualTo(Color.Black)
  }
}
