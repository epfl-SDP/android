package ch.epfl.sdp.mobile.ui.speech_recognition

import androidx.compose.runtime.State
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
interface SpeechRecognitionScreenState {

    val microphonePermissionState: State<Boolean>
    val onPermissionChange: () -> Unit

}