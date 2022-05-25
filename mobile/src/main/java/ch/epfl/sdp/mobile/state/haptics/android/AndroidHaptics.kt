package ch.epfl.sdp.mobile.state.haptics.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import ch.epfl.sdp.mobile.state.haptics.Haptics

class AndroidHaptics : Haptics {
  @Composable
  override fun HapticFeedback(key: Any) {
    val feedback = LocalHapticFeedback.current
    LaunchedEffect(key, feedback) { feedback.performHapticFeedback(HapticFeedbackType.LongPress) }
  }
}
