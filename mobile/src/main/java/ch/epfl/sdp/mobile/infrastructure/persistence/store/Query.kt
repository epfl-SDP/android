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
   * Filters the results by keeping only the documents which contain the given [value] for the given
   * [field].
   *
   * @param field the field for which the equality is checked.
   * @param value the value which is looked for.
   *
   * @return the updated [Query].
   */
  fun whereEquals(field: String, value: Any?): Query

  /**
   * Filters the results by keeping only the documents which **do not** contain the given [value]
   * for the given [field].
   *
   * @param field the field for which the difference is checked.
   * @param value the value which is avoided.
   *
   * @return the updated [Query].
   */
  fun whereNotEquals(field: String, value: Any?): Query

  /**
   * Filters the results by keeping only the documents which contain a string value prefixed with
   * the given [prefix] for the given [field].
   *
   * @param field the field for which the difference is checked.
   * @param prefix the prefix that the value should start with.
   *
   * @return the updated [Query].
   */
  fun startsWith(field: String, prefix: String): Query

  /**
   * Filters the results by keeping only the documents which contain the given [value] in an array
   * in the given [field].
   *
   * @param field the field for which array contains is checked.
   * @param value the value which is looked for.
   *
   * @return the update [Query].
   */
  fun whereArrayContains(field: String, value: Any): Query

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
