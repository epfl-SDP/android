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
   *
   * @return the updated [Query].
   */
  fun limit(count: Long): Query

  /** An enumeration representing the ordering of some values. */
  enum class Direction {

    /** A direction which returns the smallest item first. */
    Ascending,

    /** A direction which returns the greater item first. */
    Descending,
  }

  /**
   * Orders the result according to the values of the document for the given field path.
   *
   * @param path the field path for which the ordering is performed.
   * @param direction the direction in which the ordering is performed.
   *
   * @return the updated [Query].
   */
  fun orderBy(path: FieldPath, direction: Direction = Direction.Ascending): Query

  /** @see orderBy a variant with a [FieldPath]. */
  fun orderBy(field: String, direction: Direction = Direction.Ascending): Query =
      orderBy(FieldPath(field), direction)

  /**
   * Filters the results by keeping only the documents for which the value of the given field path
   * is greater than the given [value]. Documents which do not contain this field path will be
   * discarded.
   *
   * @param path the field path which is compared.
   * @param value the value which is used for comparison.
   * @param inclusive true if the results should include documents whose field is an exact match.
   *
   * @return the updated [Query].
   */
  fun whereGreaterThan(path: FieldPath, value: Any, inclusive: Boolean = true): Query

  /** @see whereGreaterThan a variant with a [FieldPath]. */
  fun whereGreaterThan(field: String, value: Any, inclusive: Boolean = true): Query =
      whereGreaterThan(FieldPath(field), value, inclusive)

  /**
   * Filters the results by keeping only the documents for which the value of the given field path
   * is less than the given [value]. Documents which do not contain this field will be discarded.
   *
   * @param path the field path which is compared.
   * @param value the value which is used for comparison.
   * @param inclusive true if the results should include documents whose field is an exact match.
   *
   * @return the updated [Query].
   */
  fun whereLessThan(path: FieldPath, value: Any, inclusive: Boolean = true): Query

  /** @see whereLessThan a variant with a [FieldPath]. */
  fun whereLessThan(field: String, value: Any, inclusive: Boolean = true): Query =
      whereLessThan(FieldPath(field), value, inclusive)

  /**
   * Filters the results by keeping only the documents which contain the given [value] for the given
   * field path.
   *
   * @param path the field path for which the equality is checked.
   * @param value the value which is looked for.
   *
   * @return the updated [Query].
   */
  fun whereEquals(path: FieldPath, value: Any?): Query

  /** @see whereEquals a variant with a [FieldPath]. */
  fun whereEquals(field: String, value: Any?): Query = whereEquals(FieldPath(field), value)

  /**
   * Filters the results by keeping only the documents which **do not** contain the given [value]
   * for the given field path.
   *
   * @param path the field path for which the difference is checked.
   * @param value the value which is avoided.
   *
   * @return the updated [Query].
   */
  fun whereNotEquals(path: FieldPath, value: Any?): Query

  /** @see whereNotEquals a variant with a [FieldPath] */
  fun whereNotEquals(field: String, value: Any?): Query = whereNotEquals(FieldPath(field), value)

  /**
   * Filters the results by keeping only the documents which contain the given [value] in an array
   * in the given field path.
   *
   * @param path the field path for which array contains is checked.
   * @param value the value which is looked for.
   *
   * @return the updated [Query].
   */
  fun whereArrayContains(path: FieldPath, value: Any): Query

  /** @see whereArrayContains a variant with a [FieldPath]. */
  fun whereArrayContains(field: String, value: Any): Query =
      whereArrayContains(FieldPath(field), value)

  /**
   * Returns a [Flow] of all the snapshots for the current [Query].
   *
   * @return the [Flow] of the [QuerySnapshot]s.
   */
  fun asQuerySnapshotFlow(): Flow<QuerySnapshot>

  /** Returns the [QuerySnapshot] of this query. */
  suspend fun getQuerySnapshot(): QuerySnapshot
}

/**
 * Returns the result of the [Query].
 *
 * @receiver the [Query]
 * @param T the type of the document.
 * @return the [List] of all the documents in the query.
 */
suspend inline fun <reified T : Any> Query.get(): List<T> =
    getQuerySnapshot().toObjects(T::class).filterNotNull()

/**
 * Returns a [Flow] to all the values in the current query.
 *
 * @param T the type of the document.
 * @return the [Flow] of a [List] of the document values.
 */
inline fun <reified T : Any> Query.asFlow(): Flow<List<T?>> =
    asQuerySnapshotFlow().map { it.toObjects(T::class) }
