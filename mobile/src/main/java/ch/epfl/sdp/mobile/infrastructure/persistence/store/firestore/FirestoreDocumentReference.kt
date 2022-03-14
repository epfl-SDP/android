package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentSnapshot
import com.google.firebase.firestore.DocumentReference as ActualDocumentReference
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * An implementation of [DocumentReference] which uses a Firestore document reference
 * under-the-hood.
 *
 * @param reference the [ActualDocumentReference].
 */
class FirestoreDocumentReference(
    private val reference: ActualDocumentReference,
) : DocumentReference {

  override fun collection(path: String): CollectionReference =
      FirestoreCollectionReference(reference.collection(path))

  override fun asDocumentSnapshotFlow(): Flow<DocumentSnapshot?> =
      callbackFlow {
            val registration =
                reference.addSnapshotListener { value, error ->
                  value?.let { trySend(FirestoreDocumentSnapshot(it)) }
                  error?.let { close(it) }
                }
            awaitClose { registration.remove() }
          }
          .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override suspend fun delete() {
    reference.delete().await()
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    val values = FirestoreDocumentEditScope().apply(scope).values
    reference.set(values, SetOptions.merge()).await()
  }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    val values = FirestoreDocumentEditScope().apply(scope).values
    reference.set(values).await()
  }

  override suspend fun <T : Any> set(value: T, valueClass: KClass<T>) {
    reference.set(value).await()
  }
}
