package ch.epfl.sdp.mobile.data.api.firebase

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

/**
 * Transforms a [Query] into a [Flow] of [QuerySnapshot]. This internally makes sure that
 * cancellation works properly with the [Flow].
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun Query.asFlow(): Flow<QuerySnapshot> {
  return callbackFlow {
        val callback =
            EventListener<QuerySnapshot> { snapshot, exception ->
              snapshot?.let { trySend(it) }
              exception?.let { close(it) }
            }
        val subscription = addSnapshotListener(callback)
        awaitClose { subscription.remove() }
      }
      .buffer(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
}

/**
 * Transforms a [DocumentReference] into a [Flow] of [DocumentSnapshot]. This internally makes sure
 * that cancellation works properly with the [Flow].
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun DocumentReference.asFlow(): Flow<DocumentSnapshot> {
  return callbackFlow {
        val callback =
            EventListener<DocumentSnapshot> { snapshot, exception ->
              snapshot?.let { trySend(it) }
              exception?.let { close(it) }
            }
        val subscription = addSnapshotListener(callback)
        awaitClose { subscription.remove() }
      }
      .buffer(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
}
