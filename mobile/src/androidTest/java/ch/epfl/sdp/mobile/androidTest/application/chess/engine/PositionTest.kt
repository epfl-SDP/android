package ch.epfl.sdp.mobile.androidTest.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PositionTest {

  @Test
  fun allPositions_hasCorrectSize() {
    assertThat(Position.all().count()).isEqualTo(64)
  }

  @Test
  fun allPositions_areInBounds() {
    val allInBounds = Position.all().all(Position::inBounds)
    assertThat(allInBounds).isTrue()
  }

  @Test
  fun negativePosition_notInBounds() {
    assertThat(Position(-1, -1).inBounds).isFalse()
    assertThat(Position(0, -1).inBounds).isFalse()
    assertThat(Position(-1, 0).inBounds).isFalse()
  }

  @Test
  fun tooFarRightPosition_notInBounds() {
    assertThat(Position(8, 0).inBounds).isFalse()
  }

  @Test
  fun tooFarBottom_position_notInBounds() {
    assertThat(Position(0, 8).inBounds).isFalse()
  }

  @Test
  fun plusDeltaInBounds_returnsPosition() {
    val end = Position(0, 0) + Delta(1, 2)
    assertThat(end).isEqualTo(Position(1, 2))
  }

  @Test
  fun plusDeltaOutOfBounds_returnsNull() {
    val end = Position(0, 0) + Delta(-1, -2)
    assertThat(end).isNull()
  }
}
