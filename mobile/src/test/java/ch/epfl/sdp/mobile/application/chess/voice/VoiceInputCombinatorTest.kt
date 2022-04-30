package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import org.junit.Assert.*
import org.junit.Test

class VoiceInputCombinatorTest {
  @Test
  fun test() {
    val res = VoiceInputCombinator.action().parse("king a1-a1").singleOrNull()?.output
    val expected = Action.Move(Position(0, 7), Delta(0, 0))

    assertEquals(expected, res)
  }
}
