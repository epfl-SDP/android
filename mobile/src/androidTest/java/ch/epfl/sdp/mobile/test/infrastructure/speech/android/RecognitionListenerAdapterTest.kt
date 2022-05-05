package ch.epfl.sdp.mobile.test.infrastructure.speech.android

import android.os.Bundle
import ch.epfl.sdp.mobile.infrastructure.speech.android.RecognitionListenerAdapter
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecognitionListenerAdapterTest {

  @Test
  fun given_emptyAdapter_when_callingAnyMethod_then_neverThrowsAndAlwaysReturnsUnit() {
    // This is slightly artificial, but we don't really expect anything else from the subject under
    // test and it's required for code coverage.
    val adapter = object : RecognitionListenerAdapter() {}

    assertThat(adapter.onReadyForSpeech(Bundle.EMPTY)).isEqualTo(Unit)
    assertThat(adapter.onBeginningOfSpeech()).isEqualTo(Unit)
    assertThat(adapter.onRmsChanged(0f)).isEqualTo(Unit)
    assertThat(adapter.onBufferReceived(byteArrayOf())).isEqualTo(Unit)
    assertThat(adapter.onEndOfSpeech()).isEqualTo(Unit)
    assertThat(adapter.onResults(Bundle.EMPTY)).isEqualTo(Unit)
    assertThat(adapter.onPartialResults(Bundle.EMPTY)).isEqualTo(Unit)
    assertThat(adapter.onEvent(0, Bundle.EMPTY)).isEqualTo(Unit)
  }
}
