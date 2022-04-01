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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

const val Lang = "en-US"
const val Max_results = 10

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SpeechRecognitionScreen(modifier: Modifier = Modifier) {

  val context = LocalContext.current
  var text by remember { mutableStateOf("---") }
  val microPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
  val activeSpeech = remember { mutableStateOf(false) }

  val microIcon = if (activeSpeech.value) PawniesIcons.Mic else PawniesIcons.MicOff

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier) {

    // Speech Text
    Text(text, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(32.dp))

    // Microphone Button
    OutlinedButton(
        shape = CircleShape,
        onClick = {
          if (!microPermissionState.hasPermission) {
            microPermissionState.launchPermissionRequest()
          }
          activeSpeech.value = !activeSpeech.value && microPermissionState.hasPermission
          text = if (activeSpeech.value) "Listening" else "---"
        }) { Icon(microIcon, null) }

    Spacer(modifier = Modifier.height(32.dp))

    // Display information about vocal permission
    PermissionText(
        hasPermission = microPermissionState.hasPermission,
    )
  }
  if (activeSpeech.value) {
    LaunchedEffect(key1 = activeSpeech) {
      val txt = recognition(context)
      text = txt.joinToString(separator = "\n")
      activeSpeech.value = false
    }
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
          .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, Max_results) // Number of results

  // Listener for results
  val listener =
      object : RecognitionListenerAdapter() {
        override fun onResults(results: Bundle?) {
          super.onResults(results)
          if (results == null) {
            cont.resume(emptyList())
            return
          }
          cont.resume(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!!)
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
