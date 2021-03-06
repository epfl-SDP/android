package ch.epfl.sdp.mobile.test.infrastructure.speech

import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory

/**
 * An implementation of [SpeechRecognizerFactory] which result is recognized but and lead to a legal
 * move.
 */
object LegalActionSpeechRecognizerFactory : SpeechRecognizerFactory {
  override fun createSpeechRecognizer() = LegalActionSpeechRecognizer()
}

class LegalActionSpeechRecognizer : SpeechRecognizer {

  companion object {

    /** The results which will always be returned on success. */
    val Results = listOf("Pawn e2 to e4", "World")
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
