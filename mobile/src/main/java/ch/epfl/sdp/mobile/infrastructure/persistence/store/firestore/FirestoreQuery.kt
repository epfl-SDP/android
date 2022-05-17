package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.infrastructure.persistence.store.QuerySnapshot
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreFieldPath.toFirestoreFieldPath
import com.google.firebase.firestore.Query as ActualQuery
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * An implementation of a [Query] which uses a Firestore query under-the-hood.
 *
 * @param reference the [ActualQuery].
 */
class FirestoreQuery(
    private val reference: ActualQuery,
) : Query {

  override fun limit(count: Long): Query = FirestoreQuery(reference.limit(count))

  private fun Query.Direction.toFirestoreDirection(): ActualQuery.Direction =
      when (this) {
        Query.Direction.Ascending -> ActualQuery.Direction.ASCENDING
        Query.Direction.Descending -> ActualQuery.Direction.DESCENDING
      }

  override fun orderBy(path: FieldPath, direction: Query.Direction): Query =
      FirestoreQuery(
          reference.orderBy(
              path.toFirestoreFieldPath(),
              direction.toFirestoreDirection(),
          ),
      )

  override fun whereGreaterThan(path: FieldPath, value: Any, inclusive: Boolean): Query =
      if (inclusive)
          FirestoreQuery(reference.whereGreaterThanOrEqualTo(path.toFirestoreFieldPath(), value))
      else FirestoreQuery(reference.whereGreaterThan(path.toFirestoreFieldPath(), value))

  override fun whereLessThan(path: FieldPath, value: Any, inclusive: Boolean): Query =
      if (inclusive)
          FirestoreQuery(reference.whereLessThanOrEqualTo(path.toFirestoreFieldPath(), value))
      else FirestoreQuery(reference.whereLessThan(path.toFirestoreFieldPath(), value))

  override fun whereEquals(path: FieldPath, value: Any?): Query =
      FirestoreQuery(reference.whereEqualTo(path.toFirestoreFieldPath(), value))

  override fun whereNotEquals(path: FieldPath, value: Any?): Query =
      FirestoreQuery(reference.whereNotEqualTo(path.toFirestoreFieldPath(), value))

  override fun whereArrayContains(path: FieldPath, value: Any): Query =
      FirestoreQuery(reference.whereArrayContains(path.toFirestoreFieldPath(), value))

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

  override suspend fun getQuerySnapshot(): QuerySnapshot =
      FirestoreQuerySnapshot(reference.get().await())
}
