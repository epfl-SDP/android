package ch.epfl.sdp.mobile.ui.speech_recognition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.coroutines.resume
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

const val Lang = "en-US"
const val MaxResultsCount = 10

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SpeechRecognitionScreen(modifier: Modifier = Modifier) {

  val context = LocalContext.current
  var text by remember { mutableStateOf("---") }
  val microPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
  var activeSpeech by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  val microIcon = if (activeSpeech) PawniesIcons.GameMicOn else PawniesIcons.GameMicOff

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
          askForPermission(microPermissionState)
          activeSpeech = !activeSpeech && microPermissionState.hasPermission
          scope.launch {
            if (activeSpeech) {
              text = "Listening..."
              text = recognition(context).joinToString(separator = "\n")
              activeSpeech = false
            } else {
              text = "---"
            }
          }
        }) { Icon(microIcon, null) }

    // Display information about vocal permission
    PermissionText(
        hasPermission = microPermissionState.hasPermission,
    )
  }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun askForPermission(microPermissionState: PermissionState) {
  if (!microPermissionState.hasPermission) {
    microPermissionState.launchPermissionRequest()
  }
}

@Composable
private fun PermissionText(modifier: Modifier = Modifier, hasPermission: Boolean = false) {
  val text = if (hasPermission) "Permission has been granted ! " else "Permission was NOT GRANTED !"
  Text(text = text, textAlign = TextAlign.Center, modifier = modifier)
}

// Returns speech result from the recognizer
suspend fun recognition(context: Context): List<String> = suspendCancellableCoroutine { cont ->
  val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
  val speechRecognizerIntent =
      Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH) // Speech action
          .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Lang) // Speech language
          .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MaxResultsCount) // Number of results

  // Listener for results
  val listener =
      object : RecognitionListenerAdapter() {
        override fun onResults(results: Bundle?) {
          super.onResults(results)
          cont.resume(
              // results cannot br null
              results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList())
        }
      }
  recognizer.setRecognitionListener(listener)
  recognizer.startListening(speechRecognizerIntent)

  // Clearing upon coroutine cancellation
  cont.invokeOnCancellation {
    recognizer.stopListening()
    recognizer.destroy()
  }
}

abstract class RecognitionListenerAdapter : RecognitionListener {
  override fun onReadyForSpeech(params: Bundle?) = Unit
  override fun onBeginningOfSpeech() = Unit
  override fun onRmsChanged(rmsdB: Float) = Unit
  override fun onBufferReceived(buffer: ByteArray?) = Unit
  override fun onEndOfSpeech() = Unit
  override fun onError(error: Int) = Unit
  override fun onResults(results: Bundle?) = Unit
  override fun onPartialResults(partialResults: Bundle?) = Unit
  override fun onEvent(eventType: Int, params: Bundle?) = Unit
}
