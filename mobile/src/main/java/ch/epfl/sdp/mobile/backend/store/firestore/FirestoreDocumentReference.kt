package ch.epfl.sdp.mobile.backend.store.firestore

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.DocumentEditScope
import ch.epfl.sdp.mobile.backend.store.DocumentReference
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

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<T?> =
      callbackFlow {
            val registration =
                reference.addSnapshotListener { value, error ->
                  value?.let { trySend(it.toObject(valueClass.java)) }
                  error?.let { close(it) }
                }
            awaitClose { registration.remove() }
          }
          .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override suspend fun delete() {
    reference.delete().await()
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    val values = FirestoreDocumentEditScope().apply(scope)
    reference.set(values, SetOptions.merge()).await()
  }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    val values = FirestoreDocumentEditScope().apply(scope).values
    reference.set(values).await()
  }
}
