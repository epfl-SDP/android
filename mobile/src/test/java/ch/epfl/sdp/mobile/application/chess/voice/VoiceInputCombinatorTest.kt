package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class VoiceInputCombinatorTest {
  @Test
  fun given_moveKing_when_parsed_then_returnMoveAction() {
    val res = VoiceInputCombinator.action().parse("king d8 to d7").map { it.output }

    val expected = Action.Move(Position(3, 0), Delta(0, 1))

    assertThat(res.contains(expected)).isTrue()
  }

  @Test
  fun given_inputPromotion_when_parsed_then_returnPromotionAction() {
    val res = VoiceInputCombinator.action().parse("d8 to d7 queen").singleOrNull()?.output
    val expected = Action.Promote(Position(3, 0), Delta(0, 1), Rank.Queen)

    assertThat(res).isEqualTo(expected)
  }

  @Test
  fun given_untreatedInput_when_parsed_then_returnCorrectPosition() {
    val res = VoiceInputCombinator.action().parse("quinn dee ate to dee 7").singleOrNull()?.output
    val expected = Action.Move(Position(3, 0), Delta(0, 1))

    assertThat(res).isEqualTo(expected)
  }

  @Test
  fun given_aListOfPossibilityInput_when_parsed_returnTheBestMatchResult() {
    val res = VoiceInput.parseInput(listOf("This is a test", "King C2 to C4"))
    val expected = Action.Move(Position(2, 6), Delta(0, -2))

    assertThat(res).isEqualTo(expected)
  }

  @Test
  fun given_aListOfNotMatchingInput_when_parsed_returnNull() {
    val res = VoiceInput.parseInput(listOf("This is a test", "This is not a test"))

    assertThat(res).isEqualTo(null)
  }

  @Test
  fun given_aInputWithFirstTokenEndingByIng_when_parsed_returnSuccessfully() {
    val res = VoiceInput.parseInput(listOf("Cooking C2 to C4"))
    val expected = Action.Move(Position(2, 6), Delta(0, -2))

    assertThat(res).isEqualTo(expected)
  }
}
