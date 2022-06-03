package ch.epfl.sdp.mobile.test.infrastructure.speech.android

import android.content.Context
import android.speech.SpeechRecognizer
import ch.epfl.sdp.mobile.infrastructure.speech.android.AndroidSpeechRecognizerFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test

class AndroidSpeechRecognizerFactoryTest {

  @Test
  fun given_factory_when_createSpeechRecognizer_then_createsFrameworkSpeechRecognizer() {
    val context = mockk<Context>()
    val factory = AndroidSpeechRecognizerFactory(context)
    val recognizer = mockk<SpeechRecognizer>()
    mockkStatic(SpeechRecognizer::class) {
      every { SpeechRecognizer.createSpeechRecognizer(context) } returns recognizer

      factory.createSpeechRecognizer()

      verify { SpeechRecognizer.createSpeechRecognizer(context) }
    }
  }
}
