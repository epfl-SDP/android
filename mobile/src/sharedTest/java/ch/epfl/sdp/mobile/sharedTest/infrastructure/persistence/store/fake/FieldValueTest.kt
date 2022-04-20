package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayRemove
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FieldValueTest {

  data class DocWithoutArray(
      val value: Any? = null,
  )

  data class DocWithArray(
      val value: List<Any>? = null,
  )

  @Test
  fun emptyDocument_union_returnsElements() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").update { arrayUnion("value", "hello", "world") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(listOf("hello", "world")))
  }

  @Test
  fun documentWithField_union_overwritesField() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithoutArray("hello"))
    store.collection("docs").document("abc").update { arrayUnion("value", "hello", "world") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(listOf("hello", "world")))
  }

  @Test
  fun documentWithArrayField_union_mergesElements() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithArray(listOf("a")))
    store.collection("docs").document("abc").update { arrayUnion("value", "b", "c") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(listOf("a", "b", "c")))
  }

  @Test
  fun documentWithArrayField_union_ignoresDuplicates() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithArray(listOf("a")))
    store.collection("docs").document("abc").update { arrayUnion("value", "b", "a") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(listOf("a", "b")))
  }

  @Test
  fun emptyDocument_remove_returnsEmptyArray() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").update { arrayRemove("value") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(emptyList()))
  }

  @Test
  fun documentWithField_remove_overwritesField() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithoutArray("hello"))
    store.collection("docs").document("abc").update { arrayRemove("value", "test") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(emptyList()))
  }

  @Test
  fun documentWithArrayField_remove_deletesElements() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithArray(listOf("a", "b", "c")))
    store.collection("docs").document("abc").update { arrayRemove("value", "a", "c") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(listOf("b")))
  }

  @Test
  fun documentWithArrayField_remove_ignoresUnknown() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithArray(listOf("a", "b", "c")))
    store.collection("docs").document("abc").update { arrayRemove("value", "d") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(listOf("a", "b", "c")))
  }

  @Test
  fun documentWithArrayField_remove_handlesDuplicates() = runTest {
    val store = emptyStore()
    store.collection("docs").document("abc").set(DocWithArray(listOf("a", "a")))
    store.collection("docs").document("abc").update { arrayRemove("value", "a") }

    val doc = store.collection("docs").document("abc").asFlow<DocWithArray>().first()
    assertThat(doc).isEqualTo(DocWithArray(emptyList()))
  }
}
