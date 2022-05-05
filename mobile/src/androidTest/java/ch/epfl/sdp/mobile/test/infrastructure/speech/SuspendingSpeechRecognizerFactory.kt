package ch.epfl.sdp.mobile.test.infrastructure.speech

import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizerFactory

/** An implementation of [SpeechRecognizerFactory] which never issues results. */
object SuspendingSpeechRecognizerFactory : SpeechRecognizerFactory {
  override fun createSpeechRecognizer(): SpeechRecognizer = SuspendingSpeechRecognizer
}

/** A [SpeechRecognizer] which never returns. */
object SuspendingSpeechRecognizer : SpeechRecognizer {
  override fun setListener(listener: SpeechRecognizer.Listener) = Unit
  override fun startListening() = Unit // Never triggers a result.
  override fun stopListening() = Unit
  override fun destroy() = Unit
}
