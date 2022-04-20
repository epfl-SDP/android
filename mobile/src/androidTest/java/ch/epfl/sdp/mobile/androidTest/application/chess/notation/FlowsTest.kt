package ch.epfl.sdp.mobile.androidTest.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.notation.mapToGame
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.androidTest.application.chess.engine.play
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FlowsTest {

  @Test
  fun given_gameFlow_when_incrementalUpdate_then_endsInCorrectState() = runTest {
    val game = Game.create().play { Position(0, 6) += Delta(0, -2) }
    val one = game.serialize()
    game.play { Position(0, 1) += Delta(0, 2) }
    val two = game.serialize()
    val games = flowOf(one, two)

    assertThat(games.mapToGame().map { it.serialize() }.toList()).containsExactly(one, two)
  }

  @Test
  fun given_gameFlow_when_nonIncrementalUpdate_then_endsInCorrectState() = runTest {
    val one = Game.create().play { Position(0, 6) += Delta(0, -2) }.serialize()
    val two = Game.create().play { Position(0, 5) += Delta(0, -2) }.serialize()
    val games = flowOf(one, two)

    assertThat(games.mapToGame().map { it.serialize() }.toList()).containsExactly(one, two)
  }
}
