package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreCollectionReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
  fun documentAutoId_callsDocument() {
    val collection = mockk<CollectionReference>()
    val document = mockk<DocumentReference>()
    val reference = FirestoreCollectionReference(collection)

    every { collection.document() } returns document
    reference.document()

    verify { collection.document() }
  }
}
