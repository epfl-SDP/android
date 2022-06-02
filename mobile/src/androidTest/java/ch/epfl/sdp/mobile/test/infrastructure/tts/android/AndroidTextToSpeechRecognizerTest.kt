package ch.epfl.sdp.mobile.test.infrastructure.tts.android

import android.speech.tts.TextToSpeech
import ch.epfl.sdp.mobile.infrastructure.tts.android.AndroidTextToSpeech
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class AndroidTextToSpeechRecognizerTest {

  @Test
  fun given_synthesizer_when_synthesizing_thenFrameworkSynthesizes() {
    val framework = mockk<TextToSpeech>()
    every { framework.speak(any(), any(), any(), any()) } returns TextToSpeech.SUCCESS
    every { framework.setLanguage(any()) } returns TextToSpeech.LANG_AVAILABLE

    val synthesizer = AndroidTextToSpeech(framework)
    synthesizer.speak("Pawnies")
    verify { framework.speak(any(), any(), any(), any()) }
  }

  @Test
  fun given_synthesizer_when_synthesizer_stops_thenFrameworkStops() {
    val framework = mockk<TextToSpeech>()
    every { framework.stop() } returns TextToSpeech.SUCCESS
    every { framework.setLanguage(any()) } returns TextToSpeech.LANG_AVAILABLE

    val synthesizer = AndroidTextToSpeech(framework)
    synthesizer.stop()
    verify { framework.stop() }
  }

  @Test
  fun given_synthesizer_when_synthesizer_shutsDown_thenFrameworkShutsDown() {
    val framework = mockk<TextToSpeech>()
    every { framework.setLanguage(any()) } returns TextToSpeech.LANG_AVAILABLE
    every { framework.shutdown() } returns Unit

    val synthesizer = AndroidTextToSpeech(framework)
    synthesizer.shutdown()
    verify { framework.shutdown() }
  }
}
