package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreDocumentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirestoreDocumentReferenceTest {

  @Test
  fun given_documentReference_when_accessingId_then_delegatesToFirestoreDocumentId() {
    val document = mockk<DocumentReference>().apply { every { id } returns "id" }
    val reference = FirestoreDocumentReference(document)

    assertThat(reference.id).isEqualTo("id")
    verify { document.id }
  }

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
  fun given_reference_when_callsGet_then_callsApi() = runTest {
    val actualDocumentReference = mockk<DocumentReference>()
    val actualDocumentSnapshot = mockk<DocumentSnapshot>()
    val documentReference = FirestoreDocumentReference(actualDocumentReference)

    every { actualDocumentReference.get() } returns Tasks.forResult(actualDocumentSnapshot)

    documentReference.getSnapshot()

    verify { actualDocumentReference.get() }
  }

  @Test
  fun given_reference_when_callsGenericGet_then_callsApi() = runTest {
    val actualDocumentReference = mockk<DocumentReference>()
    val actualDocumentSnapshot = mockk<DocumentSnapshot>()
    val documentReference = FirestoreDocumentReference(actualDocumentReference)

    every { actualDocumentReference.get() } returns Tasks.forResult(actualDocumentSnapshot)
    every { actualDocumentSnapshot.toObject<Unit>(any()) } returns Unit

    assertThat(documentReference.get<Unit>()).isEqualTo(Unit)

    verify { actualDocumentReference.get() }
    verify { actualDocumentSnapshot.toObject<Unit>(any()) }
  }

  @Test
  fun update_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every { doc.set(mapOf("key" to "value"), SetOptions.merge()) } returns Tasks.forResult(null)

    reference.update { this["key"] = "value" }

    verify { doc.set(mapOf("key" to "value"), SetOptions.merge()) }
  }
  @Test
  fun set_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every { doc.set(mapOf("key" to "value")) } returns Tasks.forResult(null)

    reference.set { this["key"] = "value" }

    verify { doc.set(mapOf("key" to "value")) }
  }

  @Test
  fun given_nestedMaps_when_callsSet_then_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every {
      doc.set(
          mapOf(
              "key1" to "value",
              "key2" to mapOf("key3" to 1),
          ),
      )
    } returns Tasks.forResult(null)

    reference.set {
      this["key1"] = "value"
      this["key2"] = mapOf("key3" to 1)
    }

    verify {
      doc.set(
          mapOf(
              "key1" to "value",
              "key2" to mapOf("key3" to 1),
          ),
      )
    }
  }

  @Test
  fun given_deeplyNestedMaps_when_callsSet_then_callsApiWithRightArguments() = runTest {
    val doc = mockk<DocumentReference>()
    val reference = FirestoreDocumentReference(doc)

    every {
      doc.set(
          mapOf(
              "key1" to "value1",
              "key2" to mapOf("key3" to mapOf("key4" to "value2")),
          ),
      )
    } returns Tasks.forResult(null)

    reference.set {
      this["key1"] = "value1"
      this["key2"] = mapOf("key3" to mapOf("key4" to "value2"))
    }

    verify {
      doc.set(
          mapOf(
              "key1" to "value1",
              "key2" to mapOf("key3" to mapOf("key4" to "value2")),
          ),
      )
    }
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
