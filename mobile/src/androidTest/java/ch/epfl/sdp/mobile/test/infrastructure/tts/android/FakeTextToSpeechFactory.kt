package ch.epfl.sdp.mobile.test.infrastructure.tts.android

import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeech
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeechFactory

/** Used fake factory of text to speech used in tests. */
object FakeTextToSpeechFactory : TextToSpeechFactory {
  override suspend fun create(): TextToSpeech {
    return FakeTextToSpeech()
  }
}

/** Fake implementation of the text to speech. */
class FakeTextToSpeech : TextToSpeech {
  override fun speak(text: String) {}
  override fun stop() {}
  override fun shutdown() {}
}
