package ch.epfl.sdp.mobile.test.state.game

import ch.epfl.sdp.mobile.application.chess.engine.Color as EngineColor
import ch.epfl.sdp.mobile.application.chess.engine.Position as EnginePosition
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toColor
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toEngineColor
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toEnginePosition
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toEngineRank
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toRank
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MatchChessBoardStateTest {

  @Test
  fun given_engineColor_when_convertingBackAndForth_then_equalsOriginalColor() {
    for (color in EngineColor.values()) {
      assertThat(color.toColor().toEngineColor()).isEqualTo(color)
    }
  }

  @Test
  fun given_enginePosition_when_convertingBackAndForth_then_equalsOriginalPosition() {
    val position = EnginePosition(1, 2)
    assertThat(position.toPosition().toEnginePosition()).isEqualTo(position)
  }

  @Test
  fun given_engineRanK_when_convertingBackAndForth_then_equalsOriginalRank() {
    for (rank in Rank.values()) {
      assertThat(rank.toRank().toEngineRank()).isEqualTo(rank)
    }
  }
}
