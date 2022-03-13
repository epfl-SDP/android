package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.infrastructure.persistence.store.QuerySnapshot
import com.google.firebase.firestore.Query as ActualQuery
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

/**
 * An implementation of a [Query] which uses a Firestore query under-the-hood.
 *
 * @param reference the [ActualQuery].
 */
class FirestoreQuery(
    private val reference: ActualQuery,
) : Query {

  override fun limit(count: Long): Query = FirestoreQuery(reference.limit(count))

  override fun asQuerySnapshotFlow(): Flow<QuerySnapshot> =
      callbackFlow<QuerySnapshot> {
            val registration =
                reference.addSnapshotListener { value, error ->
                  value?.let { trySend(FirestoreQuerySnapshot(it)) }
                  error?.let { close(it) }
                }
            awaitClose { registration.remove() }
          }
          .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
}
