package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.test.assertThrows
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FakeDocumentDocumentIdTest {

  /** Returns a document reference with the given [name] from the [Store]. */
  private fun FakeStore.doc(name: String = "doc") = collection("users").document(name)

  /** Returns a [Flow] to which [filterNotNull] and [first] are consecutively applied. */
  private suspend fun <T : Any> Flow<T?>.firstNotNull() = filterNotNull().first()

  data class DocumentWithAnnotationAndBadType(@DocumentId val id: Int? = null)
  data class DocumentWithAnnotation(@DocumentId val id: String? = null)
  data class DocumentWithoutAnnotation(val id: String? = null)

  @Test
  fun documentId_isPopulated() = runTest {
    val store = emptyStore().apply { doc("doc").set(emptyMap()) }
    assertThat(store.doc().asFlow<DocumentWithAnnotation>().firstNotNull().id).isEqualTo("doc")
  }

  @Test
  fun documentId_appliedToNonStringProperty_throws() = runTest {
    val store = emptyStore()
    store.doc().set(emptyMap())
    assertThrows<RuntimeException> {
      store.doc().asFlow<DocumentWithAnnotationAndBadType>().firstNotNull()
    }
  }

  @Test
  fun documentId_set_isIgnored() = runTest {
    val store = emptyStore()
    store.doc(name = "doc").set(DocumentWithAnnotation(id = "Test"))
    assertThat(store.doc().asFlow<DocumentWithoutAnnotation>().firstNotNull().id).isNull()
  }

  @Test
  fun documentId_appliedToConflictingField_throws() = runTest {
    val store = emptyStore()
    store.doc().set(mapOf("id" to "Hello"))
    assertThrows<RuntimeException> { store.doc().asFlow<DocumentWithAnnotation>().firstNotNull() }
  }

  @Test
  fun documentWithNoId_isGenerated() = runTest {
    val store =
        emptyStore().apply { collection("users").document().set(mapOf("name" to "matthieu")) }
    assertThat(
            store
                .collection("users")
                .whereEquals("name", "matthieu")
                .asFlow<DocumentWithAnnotation>()
                .firstNotNull())
        .hasSize(1)
  }
}
