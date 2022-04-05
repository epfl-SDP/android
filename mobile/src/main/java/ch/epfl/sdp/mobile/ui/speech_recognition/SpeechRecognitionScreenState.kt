package ch.epfl.sdp.mobile.ui.speech_recognition

import androidx.compose.runtime.State
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
interface SpeechRecognitionScreenState {

  val microphonePermissionState: State<Boolean>
  val onPermissionChange: () -> Unit
}
