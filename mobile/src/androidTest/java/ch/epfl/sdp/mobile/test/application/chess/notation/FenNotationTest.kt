package ch.epfl.sdp.mobile.test.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.parseFen
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.test.application.chess.engine.play
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FenNotationTest {

  private fun assertEquivalent(actual: Board<Piece<Color>>, expected: Board<Piece<Color>>) {
    for (x: Int in 0 until Board.Size) {
      for (y: Int in 0 until Board.Size) {
        assertThat(actual[Position(x, y)]?.rank).isEqualTo(expected[Position(x, y)]?.rank)
        assertThat(actual[Position(x, y)]?.color).isEqualTo(expected[Position(x, y)]?.color)
      }
    }
  }

  @Test
  fun given_startingBoard_when_decodingFromFenNotation_then_hasEquivalentBoard() {
    val startingGame = Game.create()
    val startingGameFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    val startingGameBoard = startingGame.board
    val deserializedBoard = parseFen(startingGameFEN).board

    assertEquivalent(deserializedBoard, startingGameBoard)
  }

  @Test
  fun given_startingBoardPlayingE4_when_decodingFromFenNotation_then_hasEquivalentBoard() {
    val foolsMateGame = Game.create().play(FoolsMate)
    val foolsMateFEN= "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3"

    val foolsMateGameBoard = foolsMateGame.board
    val deserializedBoard = parseFen(foolsMateFEN).board

    assertEquivalent(deserializedBoard, foolsMateGameBoard)
  }
}
