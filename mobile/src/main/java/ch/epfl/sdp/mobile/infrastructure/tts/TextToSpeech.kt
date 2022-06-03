package ch.epfl.sdp.mobile.infrastructure.tts

/** Interface representing the Text To Speech of the Pawnies infrastructure. */
interface TextToSpeech {

  /**
   * Synthesizes the given text.
   * @param text [String] to synthesize.
   */
  fun speak(text: String)

  /** Stops the synthesizer and all the queued requests for speech. */
  fun stop()

  /** Shuts down the text to speech service. */
  fun shutdown()
}
