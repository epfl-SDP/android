package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.speech_recognition.SpeechRecognitionScreen
import ch.epfl.sdp.mobile.ui.speech_recognition.SpeechRecognitionScreenState
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
object SpeechRecognitionScreenState : SpeechRecognitionScreenState {
  override var microphonePermissionState = mutableStateOf(false)
  override val onPermissionChange = {
    microphonePermissionState.value = !microphonePermissionState.value
  }
}

@ExperimentalPermissionsApi
@Composable
fun StatefulSpeechRecognitionScreen(user: AuthenticatedUser, modifier: Modifier = Modifier) {
  val state = SpeechRecognitionScreenState
  SpeechRecognitionScreen(state)
}
