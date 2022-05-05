package ch.epfl.sdp.mobile.test.infrastructure.speech

import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory

/** An implementation of [SpeechRecognizerFactory] which always fails. */
object FailingSpeechRecognizerFactory : SpeechRecognizerFactory {
  override fun createSpeechRecognizer() = FailingSpeechRecognizer()
}

/** A [SpeechRecognizer] which will always fail to recognize the user input, and return an error. */
class FailingSpeechRecognizer : SpeechRecognizer {
  private var listener: SpeechRecognizer.Listener? = null
  override fun setListener(listener: SpeechRecognizer.Listener) {
    this.listener = listener
  }
  override fun startListening() {
    listener?.onError()
  }
  override fun stopListening() = Unit
  override fun destroy() = Unit
}
