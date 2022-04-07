package ch.epfl.sdp.mobile.ui.speech_recognition

import androidx.compose.runtime.Stable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

/** State of the SpeechRecognition screen */
@OptIn(ExperimentalPermissionsApi::class)
@Stable
interface SpeechRecognitionScreenState {

  /* PermissionState for microphone access */
  val permissionState: PermissionState

  /* Microphone right access */
  val hasMicrophonePermission: Boolean

  /* Call back when permission is changed */
  fun onPermissionChange(): Unit
}
