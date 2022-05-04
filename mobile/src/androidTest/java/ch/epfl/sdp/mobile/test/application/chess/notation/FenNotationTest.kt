package ch.epfl.sdp.mobile.test.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation
import ch.epfl.sdp.mobile.application.chess.notation.FenNotation.parseFen
import ch.epfl.sdp.mobile.test.application.chess.engine.Games.FoolsMate
import ch.epfl.sdp.mobile.test.application.chess.engine.play
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FenNotationTest {

  private fun assertEquivalent(actual: Board<Piece<Color>>?, expected: Board<Piece<Color>>) {
    for (x: Int in 0 until Board.Size) {
      for (y: Int in 0 until Board.Size) {
        assertThat(actual?.get(Position(x, y))?.rank).isEqualTo(expected[Position(x, y)]?.rank)
        assertThat(actual?.get(Position(x, y))?.color).isEqualTo(expected[Position(x, y)]?.color)
      }
    }
  }

  @Test
  fun given_startingBoard_when_decodingFromFenNotation_then_hasEquivalentBoard() {
    val startingGame = Game.create()
    val startingGameFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    val startingGameBoard = startingGame.board
    val deserializedBoard = parseFen(startingGameFEN)?.board

    assertEquivalent(deserializedBoard, startingGameBoard)
  }

  @Test
  fun given_foolsMateFEN_when_decodingFromFenNotation_then_hasEquivalentBoard() {
    val foolsMateGame = Game.create().play(FoolsMate)
    val foolsMateFEN = "rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 1 3"

    val foolsMateGameBoard = foolsMateGame.board
    val deserializedBoard = parseFen(foolsMateFEN)?.board

    assertEquivalent(deserializedBoard, foolsMateGameBoard)
  }

  @Test
  fun given_fenWithBlackPlaying_when_decodingFromFenNotation_then_BlackIsPlaying() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    assertThat(deserializedBoardSnapshot?.playing).isEqualTo(Color.Black)
  }

  @Test
  fun given_fenWithNoEnPassant_when_decodingFromFenNotation_then_enPassantPositionIsNull() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    assertThat(deserializedBoardSnapshot?.enPassant).isNull()
  }

  @Test
  fun given_fenWithEnPassant_when_decodingFromFenNotation_then_enPassantPositionIsCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    assertThat(deserializedBoardSnapshot?.enPassant).isEqualTo(Position(2, 2))
  }

  @Test
  fun given_fenHalfMoveClock_when_decodingFromFenNotation_then_halfMoveClockIsCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    assertThat(deserializedBoardSnapshot?.halfMoveClock).isEqualTo(1)
  }

  @Test
  fun given_fenFullMoveClock_when_decodingFromFenNotation_then_fullMoveClockIsCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    assertThat(deserializedBoardSnapshot?.fullMoveClock).isEqualTo(3)
  }

  @Test
  fun given_fenNoCastlingRights_when_decodingFromFenNotation_then_castlingRightsAreCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b - c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    val expectedCastlingRights =
        FenNotation.CastlingRights(
            kingSideWhite = false,
            queenSideWhite = false,
            kingSideBlack = false,
            queenSideBlack = false,
        )
    assertThat(deserializedBoardSnapshot?.castlingRights).isEqualTo(expectedCastlingRights)
  }

  @Test
  fun given_fenAllCastlingRights_when_decodingFromFenNotation_then_castlingRightsAreCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    val expectedCastlingRights =
        FenNotation.CastlingRights(
            kingSideWhite = true,
            queenSideWhite = true,
            kingSideBlack = true,
            queenSideBlack = true,
        )
    assertThat(deserializedBoardSnapshot?.castlingRights).isEqualTo(expectedCastlingRights)
  }

  @Test
  fun given_fenNoWhiteCastlingRights_when_decodingFromFenNotation_then_castlingRightsAreCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b kq c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    val expectedCastlingRights =
        FenNotation.CastlingRights(
            kingSideWhite = false,
            queenSideWhite = false,
            kingSideBlack = true,
            queenSideBlack = true,
        )
    assertThat(deserializedBoardSnapshot?.castlingRights).isEqualTo(expectedCastlingRights)
  }

  @Test
  fun given_fenNoBlackCastlingRights_when_decodingFromFenNotation_then_castlingRightsAreCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQ c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    val expectedCastlingRights =
        FenNotation.CastlingRights(
            kingSideWhite = true,
            queenSideWhite = true,
            kingSideBlack = false,
            queenSideBlack = false,
        )
    assertThat(deserializedBoardSnapshot?.castlingRights).isEqualTo(expectedCastlingRights)
  }

  @Test
  fun given_fenNoKingsCastlingRights_when_decodingFromFenNotation_then_castlingRightsAreCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b Qq c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    val expectedCastlingRights =
        FenNotation.CastlingRights(
            kingSideWhite = false,
            queenSideWhite = true,
            kingSideBlack = false,
            queenSideBlack = true,
        )
    assertThat(deserializedBoardSnapshot?.castlingRights).isEqualTo(expectedCastlingRights)
  }

  @Test
  fun given_fenNoQueensCastlingRights_when_decodingFromFenNotation_then_castlingRightsAreCorrect() {
    val fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b Kk c6 1 3"

    val deserializedBoardSnapshot = parseFen(fen)

    val expectedCastlingRights =
        FenNotation.CastlingRights(
            kingSideWhite = true,
            queenSideWhite = false,
            kingSideBlack = true,
            queenSideBlack = false,
        )
    assertThat(deserializedBoardSnapshot?.castlingRights).isEqualTo(expectedCastlingRights)
  }
}
