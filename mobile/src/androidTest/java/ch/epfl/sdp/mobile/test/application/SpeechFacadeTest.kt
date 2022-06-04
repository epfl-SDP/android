package ch.epfl.sdp.mobile.test.application

import android.content.Context
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade.RecognitionResult.*
import ch.epfl.sdp.mobile.infrastructure.sound.android.AndroidSoundPlayer
import ch.epfl.sdp.mobile.test.infrastructure.persistence.datastore.emptyDataStoreFactory
import ch.epfl.sdp.mobile.test.infrastructure.sound.fake.FakeSoundPlayer
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuccessfulSpeechRecognizer
import ch.epfl.sdp.mobile.test.infrastructure.speech.SuspendingSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.speech.UnknownCommandSpeechRecognizerFactory
import ch.epfl.sdp.mobile.test.infrastructure.tts.android.FakeTextToSpeechFactory
import ch.epfl.sdp.mobile.test.state.StatefulGameScreenTest
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SpeechFacadeTest {

  @Test
  fun given_suspendingRecognizer_when_recognizesThenCancels_then_terminatesWithoutException() =
      runTest(UnconfinedTestDispatcher()) {
        val facade =
            SpeechFacade(
                SuspendingSpeechRecognizerFactory,
                FakeTextToSpeechFactory,
                FakeSoundPlayer,
                emptyDataStoreFactory())
        val job = launch { facade.recognize() }
        job.cancelAndJoin()
      }

  @Test
  fun given_failingRecognizer_when_recognizes_then_returnsErrorInternal() = runTest {
    val facade =
        SpeechFacade(
            FailingSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    assertThat(facade.recognize()).isEqualTo(Failure.Internal)
  }

  @Test
  fun given_successfulRecognizer_when_recognizes_then_returnsResults() = runTest {
    val facade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    assertThat(facade.recognize()).isEqualTo(Success(SuccessfulSpeechRecognizer.Results))
  }

  @Test
  fun given_fakeTextToSpeech_when_synthesizing_then_nothing_happens() = runTest {
    val facade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            FakeSoundPlayer,
            emptyDataStoreFactory())
    assertThat(facade.synthesize("Pawnies")).isEqualTo(Unit)
  }

  @Test
  fun given_speechFacade_when_synthesizing_then_playSoundCalled() = runTest {
    var called = false
    val mediaPlayer = StatefulGameScreenTest.FakeMediaPlayer(startCallback = { called = true })
    val context = mockk<Context>()
    val soundPlayer = AndroidSoundPlayer(context = context, mediaPlayer = mediaPlayer)
    val facade =
        SpeechFacade(
            UnknownCommandSpeechRecognizerFactory,
            FakeTextToSpeechFactory,
            soundPlayer,
            emptyDataStoreFactory())
    facade.synthesize("Pawnies")
    assertThat(called).isTrue()
  }
}
