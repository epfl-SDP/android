package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentSnapshot
import ch.epfl.sdp.mobile.infrastructure.persistence.store.RecordingDocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Transaction
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Transaction as ActualTransaction
import kotlin.reflect.KClass

/**
 * An implementation of a [Transaction] which uses a Firestore [ActualTransaction] under-the-hood.
 *
 * @property actual the backing [ActualTransaction].
 */
class FirestoreTransaction(
    private val actual: ActualTransaction,
) : Transaction<FirestoreDocumentReference> {

  override fun set(
      reference: FirestoreDocumentReference,
      scope: DocumentEditScope.() -> Unit,
  ) {
    val document = RecordingDocumentEditScope().also(scope).mutations.toFirestoreDocument()
    actual.set(reference.actual, document)
  }

  override fun <T : Any> set(
      reference: FirestoreDocumentReference,
      value: T,
      valueClass: KClass<T>,
  ) {
    actual.set(reference.actual, value)
  }

  override fun update(
      reference: FirestoreDocumentReference,
      scope: DocumentEditScope.() -> Unit,
  ) {
    val document = RecordingDocumentEditScope().also(scope).mutations.toFirestoreDocument()
    actual.set(reference.actual, document, SetOptions.merge())
  }

  override fun delete(
      reference: FirestoreDocumentReference,
  ) {
    actual.delete(reference.actual)
  }

  override fun getSnapshot(
      reference: FirestoreDocumentReference,
  ): DocumentSnapshot = FirestoreDocumentSnapshot(actual.get(reference.actual))
}
