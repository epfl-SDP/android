package ch.epfl.sdp.mobile.infrastructure.speech

/** A factory which can create some [SpeechRecognizer] instances. */
interface SpeechRecognizerFactory {

  /** Returns a new [SpeechRecognizer], which may be used to perform some voice recognition. */
  fun createSpeechRecognizer(): SpeechRecognizer
}
