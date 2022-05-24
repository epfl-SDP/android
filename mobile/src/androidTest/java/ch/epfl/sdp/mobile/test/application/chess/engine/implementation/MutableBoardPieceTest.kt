package ch.epfl.sdp.mobile.test.application.chess.engine.implementation

import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.PieceIdentifier
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.implementation.MutableBoardPiece
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MutableBoardPieceTest {

  @Test
  fun given_colorAndRank_when_creatingThenReadingPiece_thenColorAndRankMatch() {
    for (color in Color.values()) {
      for (rank in Rank.values()) {
        val piece = MutableBoardPiece(PieceIdentifier(0), rank, color)
        assertThat(piece.color).isEqualTo(color)
        assertThat(piece.rank).isEqualTo(rank)
      }
    }
  }

  @Test
  fun given_nonePiece_when_readingRankAndColor_then_returnsNull() {
    val piece = MutableBoardPiece.None
    assertThat(piece.rank).isNull()
    assertThat(piece.color).isNull()
  }
}
