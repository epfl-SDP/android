package ch.epfl.sdp.mobile.backend.store.firestore

import ch.epfl.sdp.mobile.backend.store.asFlow
import ch.epfl.sdp.mobile.backend.store.set
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth
import com.google.firebase.firestore.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirestoreDocumentReferenceTest {

  @Test
  fun collection_callsCollection() {
    val document = mockk<DocumentReference>()
    val collection = mockk<CollectionReference>()
    val reference = FirestoreDocumentReference(document)

    every { document.collection(any()) } returns collection
    reference.collection("path")

    verify { document.collection("path") }
  }

  @Test
  fun asFlow_successfulListener() = runTest {
    val document = mockk<DocumentReference>()
    val registration = mockk<ListenerRegistration>()
    val snapshot = mockk<DocumentSnapshot>()
    val reference = FirestoreDocumentReference(document)

    every { snapshot.toObject<String>(any()) } returns "Success"
    every { document.addSnapshotListener(any()) } answers
        { call ->
          @Suppress("UNCHECKED_CAST")
          val listener = call.invocation.args[0] as EventListener<DocumentSnapshot>
          listener.onEvent(snapshot, null)
          registration
        }
    every { registration.remove() } returns Unit

    Truth.assertThat(reference.asFlow<String>().first()).isEqualTo("Success")
  }

  @Test
  fun asFlow_failingListener() = runTest {
    val document = mockk<DocumentReference>()
    val registration = mockk<ListenerRegistration>()
    val reference = FirestoreDocumentReference(document)
    val exception =
        FirebaseFirestoreException("error", FirebaseFirestoreException.Code.PERMISSION_DENIED)

    every { document.addSnapshotListener(any()) } answers
        { call ->
          val listener = call.invocation.args[0] as EventListener<*>
          listener.onEvent(null, exception)
          registration
        }
    every { registration.remove() } returns Unit

    val caught =
        try {
          reference.asFlow<Int>().first()
          error("Should not succeed.")
        } catch (ex: FirebaseFirestoreException) {
          ex
        }

    Truth.assertThat(caught).isEqualTo(exception)
  }

  @Test
  fun delete_callsApi() = runTest {
    val document = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(document)

    every { document.delete() } returns Tasks.forResult(null)

    reference.delete()

    verify { document.delete() }
  }

  @Test
  fun update_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every { doc.set(any(), SetOptions.merge()) } returns Tasks.forResult(null)

    reference.update { this["key"] = "value" }

    verify { doc.set(any(), SetOptions.merge()) }
  }
  @Test
  fun set_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every { doc.set(mapOf("key" to "value")) } returns Tasks.forResult(null)

    reference.set { this["key"] = "value" }

    verify { doc.set(mapOf("key" to "value")) }
  }

  data class TestDocument(
      val name: String? = null,
  )

  @Test
  fun setFromClass_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)
    val sample = TestDocument("Hello")
    every { doc.set(sample) } returns Tasks.forResult(null)

    reference.set(sample)

    verify { doc.set(sample) }
  }
}
