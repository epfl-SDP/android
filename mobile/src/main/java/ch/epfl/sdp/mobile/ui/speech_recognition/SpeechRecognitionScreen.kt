package ch.epfl.sdp.mobile.ui.speech_recognition

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.createSpeechRecognizer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.util.*

@Preview(backgroundColor = 0xFFFFFBE6, showBackground = true)
@Composable
fun Preview_mic() {
  PawniesTheme { SpeechRecognitionScreen() }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun SpeechRecognitionScreen(modifier: Modifier = Modifier) {

  val text = remember { mutableStateOf("---") }
  val microPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
  var startSpeech by remember { mutableStateOf(false) }
  var speechRecognizer: SpeechRecognizer? = null

  if (startSpeech) {
    speechRecognizer = startRecognition(text)
  }

  val microIcon = if (microPermissionState.hasPermission) PawniesIcons.Mic else PawniesIcons.MicOff

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier) {
    // Speech Text
    Text(text.value, textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(32.dp))
    OutlinedButton(
        shape = CircleShape,
        onClick = {
          microPermissionState.launchPermissionRequest()
          if (microPermissionState.hasPermission) {
            startSpeech = true
          } else {
            microPermissionState.launchPermissionRequest()
          }
          if(startSpeech){
            startSpeech = false
            speechRecognizer?.stopListening()
          }
        }) { Icon(microIcon, null) }
    Spacer(modifier = Modifier.height(32.dp))
    PermissionText(
        hasPermission = microPermissionState.hasPermission,
    )
  }
}

@Composable
private fun PermissionText(hasPermission: Boolean = false, modifier: Modifier = Modifier) {
  val text = if (hasPermission) "Permission has been granted ! " else "Permission was NOT GRANTED !"
  Text(text = text, textAlign = TextAlign.Center, modifier = modifier)
}

@Composable
private fun startRecognition(text: MutableState<String>): SpeechRecognizer {

  val speechRecognizer = createSpeechRecognizer(LocalContext.current)
  val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
  speechRecognizerIntent.putExtra(
      RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
  speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

  speechRecognizer.setRecognitionListener(
      object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {
          text.value = "Listening..."
        }
        override fun onResults(results: Bundle?) {
          val speech = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          text.value = speech?.get(0)!!
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
      })

  speechRecognizer.startListening(speechRecognizerIntent)
  return speechRecognizer
}
