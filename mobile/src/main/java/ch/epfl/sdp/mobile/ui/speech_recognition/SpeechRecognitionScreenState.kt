package ch.epfl.sdp.mobile.ui.speech_recognition

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
interface SpeechRecognitionScreenState {

  val permissionState: PermissionState
  val hasMicrophonePermission: Boolean
  fun onPermissionChange(): Unit
}
