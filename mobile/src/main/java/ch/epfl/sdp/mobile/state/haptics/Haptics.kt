package ch.epfl.sdp.mobile.state.haptics

import androidx.compose.runtime.Composable

interface Haptics {
  /**
   * Performs Haptic Feedback (Phone vibration) when the given [key] changes.
   *
   * @param key the changeable key upon which the haptic feedback is performed.
   */
  @Composable fun HapticFeedback(key: Any)
}
