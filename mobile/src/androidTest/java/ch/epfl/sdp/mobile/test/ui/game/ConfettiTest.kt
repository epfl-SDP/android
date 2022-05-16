package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.ui.test.TestMonotonicFrameClock
import ch.epfl.sdp.mobile.test.assertThrows
import ch.epfl.sdp.mobile.ui.game.ConfettiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test

class ConfettiTest {

  @Test
  fun given_spawn_when_angleTooSmall_then_throws() = runTest {
    val state = ConfettiState()
    assertThrows<IllegalArgumentException> { state.spawn(angle = -1f) }
  }

  @Test
  fun given_spawn_when_angleTooBig_then_throws() = runTest {
    val state = ConfettiState()
    assertThrows<IllegalArgumentException> { state.spawn(angle = 361f) }
  }

  @Test
  fun given_spawn_when_spreadZero_then_throws() = runTest {
    val state = ConfettiState()
    assertThrows<IllegalArgumentException> { state.spawn(spread = 0f) }
  }

  @Test
  fun given_spawn_when_spreadTooBig_then_throws() = runTest {
    val state = ConfettiState()
    assertThrows<IllegalArgumentException> { state.spawn(spread = 361f) }
  }

  @Test
  fun given_spawn_when_noColors_then_throws() = runTest {
    val state = ConfettiState()
    assertThrows<IllegalArgumentException> { state.spawn(colors = emptyList()) }
  }

  @Test
  fun given_spawn_when_negativeCount_then_throws() = runTest {
    val state = ConfettiState()
    assertThrows<IllegalArgumentException> { state.spawn(count = -1) }
  }

  // TODO : Migrate to Compose 1.2 and away from TestCoroutineDispatcher.
  @Test
  fun given_spawn_when_done_then_isNotRunning() =
      runTest(TestCoroutineDispatcher()) {
        val state = ConfettiState()
        withContext(TestMonotonicFrameClock(this)) { state.spawn() }
        assertThat(state.isRunning).isFalse()
      }
}
