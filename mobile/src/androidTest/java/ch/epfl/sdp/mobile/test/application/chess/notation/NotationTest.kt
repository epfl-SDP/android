package ch.epfl.sdp.mobile.test.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.parseGame
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.Stalemate
import ch.epfl.sdp.mobile.test.application.chess.engine.play
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotationTest {

  @Test
  fun given_foolsMate_when_encodingThenDecoding_then_hasGoodBoard() {
    val foolsMate = Game.create().play(FoolsMate)

    val foolsMateBoard = foolsMate.board
    val serializedDeserializedBoard = parseGame(foolsMate.toAlgebraicNotation()).board

    assertThat(serializedDeserializedBoard).isEqualTo(foolsMateBoard)
  }

  @Test
  fun given_staleMate_when_encodingThenDecoding_then_hasGoodBoard() {
    val staleMate = Game.create().play(Stalemate)

    val foolsMateBoard = staleMate.board
    val serializedDeserializedBoard = parseGame(staleMate.toAlgebraicNotation()).board

    assertThat(serializedDeserializedBoard).isEqualTo(foolsMateBoard)
  }
}
