package ch.epfl.sdp.mobile.test.infrastructure.tts.android

import android.content.Context
import android.speech.tts.TextToSpeech
import ch.epfl.sdp.mobile.infrastructure.tts.android.AndroidTextToSpeechFactory
import ch.epfl.sdp.mobile.test.assertThrows
import io.mockk.*
import java.lang.IllegalStateException
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AndroidTextToSpeechFactoryTest {

  @Test
  fun given_factory_when_successfulCreateTextToSpeech_then_createsFrameworkSpeechRecognizer() =
      runTest {
    val context = mockk<Context>()

    val factory = AndroidTextToSpeechFactory(context)
    val synthesizer = mockk<TextToSpeech>()
    mockkConstructor(TextToSpeech::class)

    val listener = mockk<TextToSpeech.OnInitListener>()
    every { listener.onInit(any()) } answers { synthesizer }

    every { TextToSpeech(context, listener) } returns synthesizer

    factory.create()

    verify { TextToSpeech(context, listener) }
  }
  @Test
  fun given_factory_when_failToCreateTextToSpeech_then_factoryThrowsError() = runTest {
    val context = mockk<Context>()
    val factory = AndroidTextToSpeechFactory(context)

    val listener = mockk<TextToSpeech.OnInitListener>()
    every { listener.onInit(any()) } throws IllegalStateException()

    assertThrows<IllegalStateException> { factory.create() }
  }
}
