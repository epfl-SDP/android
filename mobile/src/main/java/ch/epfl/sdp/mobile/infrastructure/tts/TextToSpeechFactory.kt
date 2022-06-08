package ch.epfl.sdp.mobile.infrastructure.tts

/** Interface of the text to speech factory of the Pawnies infrastructure. */
interface TextToSpeechFactory {

  /** Creates an implementation of the Text to speech. */
  suspend fun create(): TextToSpeech
}
