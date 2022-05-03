package ch.epfl.sdp.mobile.ui.speech_recognition

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.launch

/* Extracted strings used for test, maybe removed later */
const val PermissionGranted = "Permission has been granted ! "
const val PermissionDenied = "Permission was NOT GRANTED !"
const val DefaultText = "---"
const val ListeningText = "Listening..."
const val MicroIconDescription = "micro"

/** Mutator used for cancelling a concurrent speech if the user mutes the mic */
private val mutex = MutatorMutex()

/**
 * Screen for demonstrating the SpeechRecognition android feature
 * @param state State of the screen
 * @param recognizer [SpeechRecognizerEntity] entity used in speech recognition for this screen
 * @param modifier [Modifier] of this composable
 */
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SpeechRecognitionScreen(
    state: SpeechRecognitionScreenState,
    modifier: Modifier = Modifier,
    recognizer: SpeechRecognizable = SpeechRecognizerEntity(),
) {

  val context = LocalContext.current
  var text by remember { mutableStateOf(DefaultText) }
  var activeSpeech by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  val microIcon = if (activeSpeech) PawniesIcons.GameMicOn else PawniesIcons.GameMicOff

  /**
   * Blocking function to ensure that only the most recent call to the block function is executed
   * and older executions are cancelled
   *
   * @param block: a suspending function namely the speech recognition routine
   */
  suspend fun vocalize(block: suspend () -> Unit) {
    mutex.mutate(MutatePriority.UserInput) {
      try {
        block()
      } finally {
        activeSpeech = false
      }
    }
  }

  Column(
      verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier) {

    // Speech Text
    Text(text, textAlign = TextAlign.Center)
    // Microphone Button
    OutlinedButton(
        shape = CircleShape,
        onClick = {
          askForPermission(state.permissionState, state::onPermissionChange)
          activeSpeech = !activeSpeech && state.hasMicrophonePermission
          scope.launch {
            vocalize {
              if (activeSpeech) {
                text = ListeningText
                text = recognizer.recognition(context).joinToString(separator = "\n")
              } else {
                text = DefaultText
              }
            }
          }
        }) { Icon(microIcon, MicroIconDescription) }

    // Display information about vocal permission
    PermissionText(
        hasPermission = state.hasMicrophonePermission,
    )
  }
}

/**
 * Asks the user to grant permission to use the mic if not already granted
 * @param microPermissionState permission state of microphone
 * @param onPermissionChange call back to change permission un the state
 */
@OptIn(ExperimentalPermissionsApi::class)
private fun askForPermission(
    microPermissionState: PermissionState,
    onPermissionChange: () -> Unit
) {
  if (!microPermissionState.hasPermission) {
    microPermissionState.launchPermissionRequest()
    onPermissionChange()
  }
}

/**
 * Composable responsible for displaying the permission text
 * @param hasPermission True if the permission was granted false otherwise
 * @param modifier [Modifier] for this composable
 */
@Composable
private fun PermissionText(modifier: Modifier = Modifier, hasPermission: Boolean = false) {
  val text = if (hasPermission) PermissionGranted else PermissionDenied
  Text(text = text, textAlign = TextAlign.Center, modifier = modifier)
}
