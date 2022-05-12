package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.*
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuccessfulSpeechRecognizer
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuspendingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.UnknownCommandSpeechRecognizerFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SpeechFacadeTest {

  @Test
  fun given_suspendingRecognizer_when_recognizesThenCancels_then_terminatesWithoutException() =
      runTest(UnconfinedTestDispatcher()) {
        val facade = SpeechFacade(SuspendingSpeechRecognizerFactory)
        val job = launch { facade.recognize() }
        job.cancelAndJoin()
      }

  @Test
  fun given_failingRecognizer_when_recognizes_then_returnsErrorInternal() = runTest {
    val facade = SpeechFacade(FailingSpeechRecognizerFactory)
    assertThat(facade.recognize()).isEqualTo(Failure.Internal)
  }

  @Test
  fun given_successfulRecognizer_when_recognizes_then_returnsResults() = runTest {
    val facade = SpeechFacade(UnknownCommandSpeechRecognizerFactory)
    assertThat(facade.recognize()).isEqualTo(Success(SuccessfulSpeechRecognizer.Results))
  }
}
