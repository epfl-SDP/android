package ch.epfl.sdp.mobile.test.application.chess

import ch.epfl.sdp.mobile.application.chess.Position
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PositionTest {

  @Test
  fun allPositions_hasCorrectSize() {
    assertThat(Position.all().count()).isEqualTo(64)
  }

  @Test
  fun allPositions_areInBounds() {
    val allInBounds = Position.all().fold(true) { inBounds, pos -> inBounds && pos.inBounds }
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
}
