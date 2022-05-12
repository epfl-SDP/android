package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreDocumentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreStore
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreTransaction
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentReference as ActualDocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore as ActualFirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction as ActualTransaction
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirestoreTransactionTest {

  @Test
  fun given_store_when_runsTransaction_then_callsFirestoreMethod() = runTest {
    val actualStore = mockk<ActualFirebaseFirestore>()
    val actualTransaction = mockk<ActualTransaction>()
    val store = FirestoreStore(actualStore)

    every { actualStore.runTransaction<Int>(any()) } answers
        {
          val function = firstArg<ActualTransaction.Function<Int>>()
          Tasks.forResult(function.apply(actualTransaction))
        }

    assertThat<Int>(store.transaction { 123 }).isEqualTo(123)

    verify { actualStore.runTransaction<Int>(any()) }
  }

  @Test
  fun given_transaction_when_set_then_callsFirestoreMethod() {
    val actualTransaction = mockk<ActualTransaction>()
    val actualReference = mockk<ActualDocumentReference>()
    val transaction = FirestoreTransaction(actualTransaction)
    val reference = FirestoreDocumentReference(actualReference)

    every { actualTransaction.set(actualReference, mapOf("hello" to "world")) } returns
        actualTransaction

    transaction.set(reference) { this["hello"] = "world" }

    verify { actualTransaction.set(actualReference, mapOf("hello" to "world")) }
  }

  @Test
  fun given_transaction_when_setGeneric_then_callsFirestoreMethod() {
    val actualTransaction = mockk<ActualTransaction>()
    val actualReference = mockk<ActualDocumentReference>()
    val transaction = FirestoreTransaction(actualTransaction)
    val reference = FirestoreDocumentReference(actualReference)

    every { actualTransaction.set(actualReference, any()) } returns actualTransaction

    transaction.set(reference, Any())

    verify { actualTransaction.set(actualReference, any()) }
  }

  @Test
  fun given_transaction_when_update_then_callsFirestoreMethod() {
    val actualTransaction = mockk<ActualTransaction>()
    val actualReference = mockk<ActualDocumentReference>()
    val transaction = FirestoreTransaction(actualTransaction)
    val reference = FirestoreDocumentReference(actualReference)

    every {
      actualTransaction.set(actualReference, mapOf("hello" to "world"), SetOptions.merge())
    } returns actualTransaction

    transaction.update(reference) { set("hello", "world") }

    verify { actualTransaction.set(actualReference, mapOf("hello" to "world"), SetOptions.merge()) }
  }

  @Test
  fun given_transaction_when_delete_then_callsFirestoreMethod() {
    val actualTransaction = mockk<ActualTransaction>()
    val actualReference = mockk<ActualDocumentReference>()
    val transaction = FirestoreTransaction(actualTransaction)
    val reference = FirestoreDocumentReference(actualReference)

    every { actualTransaction.delete(actualReference) } returns actualTransaction

    transaction.delete(reference)

    verify { actualTransaction.delete(actualReference) }
  }

  @Test
  fun given_transaction_when_getGeneric_then_callsFirestoreMethod() {
    val actualTransaction = mockk<ActualTransaction>()
    val actualReference = mockk<ActualDocumentReference>()
    val actualSnapshot = mockk<DocumentSnapshot>()
    val transaction = FirestoreTransaction(actualTransaction)
    val reference = FirestoreDocumentReference(actualReference)

    val result = emptyMap<Nothing, Nothing>()

    every { actualTransaction.get(actualReference) } returns actualSnapshot
    every { actualSnapshot.toObject<Map<*, *>>(any()) } returns result

    assertThat(transaction.get<Map<Nothing, Nothing>>(reference)).isEqualTo(result)

    verify { actualTransaction.get(actualReference) }
    verify { actualSnapshot.toObject<Map<*, *>>(any()) }
  }
}
