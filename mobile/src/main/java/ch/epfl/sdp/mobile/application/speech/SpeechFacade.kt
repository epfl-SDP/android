package ch.epfl.sdp.mobile.application.speech

import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.*
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * A facade which provides access to functions to perform some voice recognition.
 *
 * @param factory the [SpeechRecognizerFactory] which is used internally by this [SpeechFacade].
 */
class SpeechFacade(private val factory: SpeechRecognizerFactory) {

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
    data class Success(val results: Pair<ChessBoardState.Position, ChessBoardState.Position>) :
        RecognitionResult
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

          override fun onResults(
              results: Pair<ChessBoardState.Position, ChessBoardState.Position>?
          ) {
            cleanup()
            // TODO : it's duplicate with [AndroidSpeechRecognizer] ?
            if (results == null) {
              cont.resume(Failure.Internal)
            } else {
              cont.resume(Success(results))
            }
          }
        },
    )
    recognizer.startListening()
    cont.invokeOnCancellation { cleanup() }
  }
}
