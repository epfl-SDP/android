package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * An interface representing a query which is performed on the database. Queries return a list of
 * items and can be observed.
 *
 * @see CollectionReference the standard way to retrieve a [Query].
 */
interface Query {

  /**
   * Limits the number of items which will be returned as part of the resulting [Query].
   *
   * @param count the number of items which will be returned.
   */
  fun limit(count: Long): Query

  /**
   * Returns a [Flow] of all the snapshots for the current [Query].
   *
   * @return the [Flow] of the [QuerySnapshot]s.
   */
  fun asQuerySnapshotFlow(): Flow<QuerySnapshot>
}

/**
 * Returns a [Flow] to all the values in the current query.
 *
 * @param T the type of the document.
 * @return the [Flow] of a [List] of the document values.
 */
inline fun <reified T : Any> Query.asFlow(): Flow<List<T?>> =
    asQuerySnapshotFlow().map { it.toObjects(T::class) }
