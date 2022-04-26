package ch.epfl.sdp.mobile.test.application.chess.engine.rules

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ActionTest {

  @Test
  fun given_action_when_readsComponent1_then_readsPosition() {
    val position = Position(1, 2)
    val delta = Delta(3, 4)
    val action = Action.Move(position, delta)
    assertThat(action.component1()).isEqualTo(position)
  }

  @Test
  fun given_action_when_readsComponent2_then_readsDelta() {
    val position = Position(1, 2)
    val delta = Delta(3, 4)
    val action = Action.Move(position, delta)
    assertThat(action.component2()).isEqualTo(delta)
  }
}
