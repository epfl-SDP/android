package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import org.junit.Assert.*
import org.junit.Test

class VoiceInputCombinatorTest {
  @Test
  fun given_moveKing_when_parsed_then_returnMoveAction() {
    val res = VoiceInputCombinator.action().parse("king d8 to d7").singleOrNull()?.output
    val expected = Action.Move(Position(3, 0), Delta(0, 1))

    assertEquals(expected, res)
  }

  @Test
  fun given_inputPromotion_when_parsed_then_returnPromotionAction() {
    val res = VoiceInputCombinator.action().parse("d8 to d7 queen").singleOrNull()?.output
    val expected = Action.Promote(Position(3, 0), Delta(0, 1), Rank.Queen)

    assertEquals(expected, res)
  }
}
