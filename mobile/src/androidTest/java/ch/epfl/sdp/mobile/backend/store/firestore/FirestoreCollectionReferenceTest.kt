package ch.epfl.sdp.mobile.backend.store.firestore

import ch.epfl.sdp.mobile.backend.store.asFlow
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.*
import com.google.firebase.firestore.FirebaseFirestoreException.Code.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirestoreCollectionReferenceTest {

  @Test
  fun document_callsDocument() {
    val collection = mockk<CollectionReference>()
    val document = mockk<DocumentReference>()
    val reference = FirestoreCollectionReference(collection)

    every { collection.document(any()) } returns document
    reference.document("path")

    verify { collection.document("path") }
  }

  @Test
  fun asFlow_successfulListener() = runTest {
    val collection = mockk<CollectionReference>()
    val registration = mockk<ListenerRegistration>()
    val snapshot = mockk<QuerySnapshot>()
    val reference = FirestoreCollectionReference(collection)

    every { snapshot.toObjects<String>(any()) } returns listOf("Success")
    every { collection.addSnapshotListener(any()) } answers
        { call ->
          @Suppress("UNCHECKED_CAST")
          val listener = call.invocation.args[0] as EventListener<QuerySnapshot>
          listener.onEvent(snapshot, null)
          registration
        }
    every { registration.remove() } returns Unit

    assertThat(reference.asFlow<List<String>>().first()).isEqualTo(listOf("Success"))
  }

  @Test
  fun asFlow_failingListener() = runTest {
    val collection = mockk<CollectionReference>()
    val registration = mockk<ListenerRegistration>()
    val reference = FirestoreCollectionReference(collection)
    val exception = FirebaseFirestoreException("error", PERMISSION_DENIED)

    every { collection.addSnapshotListener(any()) } answers
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

    assertThat(caught).isEqualTo(exception)
  }
}
