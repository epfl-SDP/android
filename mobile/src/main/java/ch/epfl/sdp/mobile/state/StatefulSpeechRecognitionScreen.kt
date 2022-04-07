package ch.epfl.sdp.mobile.state

import android.Manifest
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.speech_recognition.SpeechRecognitionScreen
import ch.epfl.sdp.mobile.ui.speech_recognition.SpeechRecognitionScreenState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
class DefaultSpeechRecognitionScreenState(
    override val permissionState: PermissionState,
    private val microphonePermissionState: MutableState<Boolean>
) : SpeechRecognitionScreenState {

  override var hasMicrophonePermission by microphonePermissionState
  override fun onPermissionChange() {
    hasMicrophonePermission = permissionState.hasPermission

  }
}

@ExperimentalPermissionsApi
@Composable
fun StatefulSpeechRecognitionScreen(user: AuthenticatedUser, modifier: Modifier = Modifier) {

  val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
  val microphonePermissionState = remember(permissionState.hasPermission) { mutableStateOf(permissionState.hasPermission) }

  val state = remember(permissionState, microphonePermissionState) {
    DefaultSpeechRecognitionScreenState(permissionState, microphonePermissionState)
  }

  SpeechRecognitionScreen(state = state, modifier)
}
