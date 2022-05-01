package ch.epfl.sdp.mobile.test.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.parseGame
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.parseFen
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.Stalemate
import ch.epfl.sdp.mobile.test.application.chess.engine.play
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FenNotationTest {

  @Test
  fun given_startingBoard_when_decodingFromFenNotation_then_hasEquivalentBoard() {
    val startingGame = Game.create()
    val startingGameFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    val startingGameBoard = startingGame.board
    val deserializedBoard = parseFen(startingGameFEN).board

    for (x: Int in 0 until Board.Size) {
      for (y: Int in 0 until Board.Size) {
        assertThat(startingGameBoard[Position(x, y)]?.rank).isEqualTo(deserializedBoard[Position(x, y)]?.rank)
        assertThat(startingGameBoard[Position(x, y)]?.color).isEqualTo(deserializedBoard[Position(x, y)]?.color)
      }
    }
  }

  @Test
  fun given_startingBoard_when_decodingFromFenNotation_then_hasEqualBoard() {
    val startingGame = Game.create()
    val startingGameFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    val startingGameBoard = startingGame.board
    val deserializedBoard = parseFen(startingGameFEN).board

    assertThat(deserializedBoard).isEqualTo(startingGameBoard)
  }

  @Test
  fun given_startingBoardPlayingE4_when_decodingFromFenNotation_then_hasEqualBoard() {
    val foolsMateGame = Game.create().play(FoolsMate)
    val foolsMateFEN= "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3"

    val foolsMateGameBoard = foolsMateGame.board
    val deserializedBoard = parseFen(foolsMateFEN).board

    assertThat(deserializedBoard).isEqualTo(foolsMateGameBoard)
  }
}
