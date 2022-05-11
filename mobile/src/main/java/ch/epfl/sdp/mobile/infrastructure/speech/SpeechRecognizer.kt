package ch.epfl.sdp.mobile.infrastructure.speech

import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/** An interface providing access to the native [SpeechRecognizer] of the platform. */
interface SpeechRecognizer {

  /** A listener which will be called when some new speech recognition results are available. */
  interface Listener {

    /** A callback method, called when there's an error during the recognition. */
    fun onError()

    /**
     * A callback method, called with the list of results.
     *
     * @param results the [List] of speech recognition results, ordered by decreasing score.
     */
    fun onResults(results: List<String>)
  }

  /**
   * Sets the [Listener] for this [SpeechRecognizer].
   *
   * @param listener the [Listener] which is set.
   */
  fun setListener(listener: Listener)

  /** Starts listening with this [SpeechRecognizer]. */
  fun startListening()

  /** Stops listening with this [SpeechRecognizer]. */
  fun stopListening()

  /** Destroys the recognizer. */
  fun destroy()
}
