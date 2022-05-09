package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction as ActualTransaction
import kotlin.reflect.KClass

class FirestoreTransaction(
    private val transaction: ActualTransaction,
) : Transaction<FirestoreDocumentReference> {

  override fun set(reference: FirestoreDocumentReference, scope: DocumentEditScope.() -> Unit) {
    val document = RecordingDocumentEditScope().apply(scope).mutations.toFirestoreDocument()
    transaction.set(reference.actual, document)
  }

  override fun <T : Any> set(
      reference: FirestoreDocumentReference,
      value: T,
      valueClass: KClass<T>
  ) {
    transaction.set(reference.actual, value)
  }

  override fun update(reference: FirestoreDocumentReference, scope: DocumentEditScope.() -> Unit) {
    val document = RecordingDocumentEditScope().apply(scope).mutations.toFirestoreDocument()
    transaction.set(reference.actual, document, SetOptions.merge())
  }

  override fun delete(reference: FirestoreDocumentReference) {
    transaction.delete(reference.actual)
  }

  override fun getSnapshot(
      reference: FirestoreDocumentReference,
  ): DocumentSnapshot = transaction.get(reference.actual).let(::FirestoreDocumentSnapshot)
}
