package ch.epfl.sdp.mobile.test.infrastructure.speech

import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory

/**
 * An implementation of [SpeechRecognizerFactory] which is recognized but the resulting action is
 * illegal.
 */
object IllegalActionSpeechRecognizerFactory : SpeechRecognizerFactory {
  override fun createSpeechRecognizer() = IllegalActionSpeechRecognizer()
}

class IllegalActionSpeechRecognizer : SpeechRecognizer {

  companion object {

    /** The results which will always be returned on success. */
    val Results = listOf("King a3 to b3", "World")
  }

  private var listener: SpeechRecognizer.Listener? = null
  override fun setListener(listener: SpeechRecognizer.Listener) {
    this.listener = listener
  }
  override fun startListening() {
    listener?.onResults(Results)
  }
  override fun stopListening() = Unit
  override fun destroy() = Unit
}
