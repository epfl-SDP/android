package ch.epfl.sdp.mobile.backend.store.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class FirestoreStoreTest {

  @Test
  fun collection_callsCollection() {
    val firestore = mockk<FirebaseFirestore>()
    val collection = mockk<CollectionReference>()
    val store = FirestoreStore(firestore)

    every { firestore.collection(any()) } returns collection
    store.collection("path")

    verify { firestore.collection("path") }
  }
}
