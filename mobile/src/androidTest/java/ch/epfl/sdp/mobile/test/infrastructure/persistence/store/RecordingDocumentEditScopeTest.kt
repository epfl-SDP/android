package ch.epfl.sdp.mobile.test.infrastructure.persistence.store

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import ch.epfl.sdp.mobile.infrastructure.persistence.store.RecordingDocumentEditScope
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RecordingDocumentEditScopeTest {

  @Test
  fun given_recordingScope_when_notNestedValues_then_returnsRightResults() {
    val scope = RecordingDocumentEditScope()
    scope["hello"] = "world"

    assertThat(scope.mutations).containsExactly(FieldPath("hello"), "world")
  }

  @Test
  fun given_recordingScope_when_nestedValues_then_returnsRightResults() {
    val scope = RecordingDocumentEditScope()
    scope["k1"] = mapOf("k2" to "v")
    val expected = mapOf(FieldPath(listOf("k1", "k2")) to "v")
    assertThat(scope.mutations).containsExactlyEntriesIn(expected)
  }

  @Test
  fun given_recordingScope_when_deeplyNestedValues_then_returnsRightResults() {
    val scope = RecordingDocumentEditScope()
    scope["k1"] = mapOf("k2" to mapOf("k3" to "v"))
    val expected = mapOf(FieldPath(listOf("k1", "k2", "k3")) to "v")
    assertThat(scope.mutations).containsExactlyEntriesIn(expected)
  }
}
