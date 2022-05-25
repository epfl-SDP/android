package ch.epfl.sdp.mobile.application.speech

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.*
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * A facade which provides access to functions to perform some voice recognition.
 *
 * @param factory the [SpeechRecognizerFactory] which is used internally by this [SpeechFacade].
 */
class SpeechFacade(private val factory: SpeechRecognizerFactory, private val ttsFactory: TextToSpeechFactory) {

  /** The result of a call to [SpeechFacade.recognize]. */
  sealed interface RecognitionResult {

    /** Indicates that a failure occurred. */
    sealed interface Failure : RecognitionResult {

      /** Indicates that a failure occurred and no speech was recognized. */
      object Internal : Failure
    }

    /**
     * Indicates a success of recognition. The available [results] are sorted by decreasing score
     * (the most relevant results come first).
     *
     * @param results the [List] of possible results.
     */
    data class Success(val results: List<String>) : RecognitionResult
  }

  /**
   * Starts voice recognition, and returns the associated [RecognitionResult].
   *
   * @return the [RecognitionResult] from the recognition request.
   */
  suspend fun recognize(): RecognitionResult = suspendCancellableCoroutine { cont ->
    val recognizer = factory.createSpeechRecognizer()

    /** Cleans up the recognizer. */
    fun cleanup() {
      recognizer.stopListening()
      recognizer.destroy()
    }

    recognizer.setListener(
        object : SpeechRecognizer.Listener {
          override fun onError() {
            cleanup()
            cont.resume(Failure.Internal)
          }

          override fun onResults(results: List<String>) {
            cleanup()
            cont.resume(Success(results))
          }
        },
    )
    recognizer.startListening()
    cont.invokeOnCancellation { cleanup() }
  }

   var muted by mutableStateOf(false)
   private val tts = suspend {  ttsFactory.create() }

  suspend fun synthesize(text: String) {
    if(!muted){
      tts().speak(text)
    }
  }
}
