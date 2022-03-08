package ch.epfl.sdp.mobile.backend.store.firestore

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.DocumentReference
import com.google.firebase.firestore.CollectionReference as ActualCollectionReference
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

/**
 * An implementation of [CollectionReference] which uses a Firestore collection reference
 * under-the-hood.
 *
 * @param reference the [ActualCollectionReference].
 */
class FirestoreCollectionReference(
    private val reference: ActualCollectionReference,
) : CollectionReference {

  override fun document(path: String): DocumentReference =
      FirestoreDocumentReference(reference.document(path))

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>> =
      callbackFlow {
            val registration =
                reference.addSnapshotListener { value, error ->
                  value?.let { trySend(it.toObjects(valueClass.java)) }
                  error?.let { close(it) }
                }
            awaitClose { registration.remove() }
          }
          .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
}
