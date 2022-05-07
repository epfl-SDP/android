package ch.epfl.sdp.mobile.test.infrastructure.speech

import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory
import ch.epfl.sdp.mobile.ui.game.ChessBoardState

/** An implementation of [SpeechRecognizerFactory] which always succeeds. */
object SuccessfulSpeechRecognizerFactory : SpeechRecognizerFactory {
  override fun createSpeechRecognizer() = SuccessfulSpeechRecognizer()
}

/** A [SpeechRecognizer] which always succeeds to recognize the user input, and return [Results]. */
class SuccessfulSpeechRecognizer : SpeechRecognizer {

  companion object {

    /** The results which will always be returned on success. */
    val Results = Pair(ChessBoardState.Position(0, 0), ChessBoardState.Position(0, 0))
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
