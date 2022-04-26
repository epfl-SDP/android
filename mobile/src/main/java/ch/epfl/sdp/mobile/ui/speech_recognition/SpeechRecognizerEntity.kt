package ch.epfl.sdp.mobile.ui.speech_recognition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

val defaultListener: (CancellableContinuation<List<String>>) -> RecognitionListener = { cont ->
  object : SpeechRecognizerEntity.RecognitionListenerAdapter() {
    override fun onResults(results: Bundle?) {
      super.onResults(results)
      cont.resume(
          // results cannot be null
          results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList())
    }
  }
}

class SpeechRecognizerEntity(
    private val lang: String = "en-US",
    private val maxResultsCount: Int = 10,
    private val listener: CancellableContinuation<List<String>>.() -> RecognitionListener =
        defaultListener
) : SpeechRecognizable {
  /**
   * Returns speech results from the speech recognizer
   * @param context [Context] context of the app execution
   * @return List of size maximum [maxResultsCount] of speech recognizer results as strings
   */
  override suspend fun recognition(context: Context): List<String> =
      suspendCancellableCoroutine { cont ->
    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val speechRecognizerIntent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH) // Speech action
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang) // Speech language
            .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResultsCount) // Number of results
            .putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000)

    // Listener for results
    recognizer.setRecognitionListener(listener(cont))
    recognizer.startListening(speechRecognizerIntent)

    // Clearing upon coroutine cancellation
    cont.invokeOnCancellation {
      recognizer.stopListening()
      recognizer.destroy()
    }
  }

  /** Adapter class for Recognition' listener */
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
}
