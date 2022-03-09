package ch.epfl.sdp.mobile.backend.store.firestore

import com.google.firebase.firestore.*
import com.google.firebase.firestore.FirebaseFirestoreException.Code.*
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
}
