package ch.epfl.sdp.mobile.test.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.UCINotation.parseActionList
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UciNotationTest {

  @Test
  fun given_simpleMoves_when_decodingFromUCINotation_then_hasEqualActions() {
    val uci = "d3d6 f8d8 d6d8 a8h1"
    val res = parseActionList(uci)
    val expected =
        listOf(
            Action.Move(Position(3, 5), Position(3, 2)),
            Action.Move(Position(5, 0), Position(3, 0)),
            Action.Move(Position(3, 2), Position(3, 0)),
            Action.Move(Position(0, 0), Position(7, 7)),
        )

    assertThat(res).containsExactlyElementsIn(expected).inOrder()
  }

  @Test
  fun given_promotionMoves_when_decodingFromUCINotation_then_hasEqualActions() {
    val uci = "e7e8q h2h1n b7b8r a2a1b"
    val res = parseActionList(uci)
    val expected =
        listOf(
            Action.Promote(Position(4, 1), Position(4, 0), Rank.Queen),
            Action.Promote(Position(7, 6), Position(7, 7), Rank.Knight),
            Action.Promote(Position(1, 1), Position(1, 0), Rank.Rook),
            Action.Promote(Position(0, 6), Position(0, 7), Rank.Bishop),
        )

    assertThat(res).containsExactlyElementsIn(expected).inOrder()
  }

  @Test
  fun given_mixOfMoves_when_decodingFromUCINotation_then_hasEqualActions() {
    val uci = "d3d6 e7e8q h2h1n f8d8 d6d8 b7b8r a2a1b a8h1"
    val res = parseActionList(uci)
    val expected =
        listOf(
            Action.Move(Position(3, 5), Position(3, 2)),
            Action.Promote(Position(4, 1), Position(4, 0), Rank.Queen),
            Action.Promote(Position(7, 6), Position(7, 7), Rank.Knight),
            Action.Move(Position(5, 0), Position(3, 0)),
            Action.Move(Position(3, 2), Position(3, 0)),
            Action.Promote(Position(1, 1), Position(1, 0), Rank.Rook),
            Action.Promote(Position(0, 6), Position(0, 7), Rank.Bishop),
            Action.Move(Position(0, 0), Position(7, 7)),
        )

    assertThat(res).containsExactlyElementsIn(expected).inOrder()
  }
}
