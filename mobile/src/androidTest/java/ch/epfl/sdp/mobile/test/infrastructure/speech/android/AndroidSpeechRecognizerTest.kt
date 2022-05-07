package ch.epfl.sdp.mobile.test.infrastructure.speech.android

import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import androidx.core.os.bundleOf
import ch.epfl.sdp.mobile.infrastructure.speech.SpeechRecognizer.Listener
import ch.epfl.sdp.mobile.infrastructure.speech.android.AndroidSpeechRecognizer
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class AndroidSpeechRecognizerTest {

  @Test
  fun given_recognizer_when_destroy_then_destroysFramework() {
    val framework = mockk<SpeechRecognizer>()
    val recognizer = AndroidSpeechRecognizer(framework)
    every { framework.destroy() } returns Unit

    recognizer.destroy()

    verify { framework.destroy() }
  }

  @Test
  fun given_recognizer_when_startListening_then_startsFrameworkListening() {
    val framework = mockk<SpeechRecognizer>()
    val recognizer = AndroidSpeechRecognizer(framework)
    every { framework.startListening(any()) } returns Unit

    recognizer.startListening()

    verify { framework.startListening(any()) }
  }

  @Test
  fun given_recognizer_when_stopListening_then_stopsFrameworkListening() {
    val framework = mockk<SpeechRecognizer>()
    val recognizer = AndroidSpeechRecognizer(framework)
    every { framework.stopListening() } returns Unit

    recognizer.stopListening()

    verify { framework.stopListening() }
  }

  @Test
  fun given_recognizer_when_settingListener_then_callsErrorListener() {
    val framework = mockk<SpeechRecognizer>()
    val recognizer = AndroidSpeechRecognizer(framework)
    val listener = mockk<Listener>()

    every { listener.onError() } returns Unit
    every { framework.setRecognitionListener(any()) } answers
        {
          firstArg<RecognitionListener>().onError(0)
        }

    recognizer.setListener(listener)

    verify { listener.onError() }
  }

  @Test
  fun given_recognizer_when_settingListener_then_callsSuccessListener() {
    val framework = mockk<SpeechRecognizer>()
    val recognizer = AndroidSpeechRecognizer(framework)
    val listener = mockk<Listener>()

    val result = Pair(ChessBoardState.Position(0, 5), ChessBoardState.Position(0, 4))

    every { listener.onResults(result) } returns Unit
    every { framework.setRecognitionListener(any()) } answers
        {
          firstArg<RecognitionListener>()
              .onResults(bundleOf(RESULTS_RECOGNITION to arrayListOf("King A3 to A4")))
        }

    recognizer.setListener(listener)

    verify { listener.onResults(result) }
  }

  @Test
  fun given_recognizer_when_settingListener_then_callsErrorListerIfNoMatch() {
    val framework = mockk<SpeechRecognizer>()
    val recognizer = AndroidSpeechRecognizer(framework)
    val listener = mockk<Listener>()

    every { listener.onError() } returns Unit
    every { listener.onResults(null) } returns Unit
    every { framework.setRecognitionListener(any()) } answers
        {
          firstArg<RecognitionListener>()
              .onResults(bundleOf(RESULTS_RECOGNITION to arrayListOf("fff")))
        }

    recognizer.setListener(listener)

    verify { listener.onError() }
  }
}
