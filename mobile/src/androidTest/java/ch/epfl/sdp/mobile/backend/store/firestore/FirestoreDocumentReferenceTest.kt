package ch.epfl.sdp.mobile.backend.store.firestore

import ch.epfl.sdp.mobile.backend.store.asFlow
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
}
