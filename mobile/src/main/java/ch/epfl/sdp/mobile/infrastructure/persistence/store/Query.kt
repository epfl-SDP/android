package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

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
   * Returns a [Flow] to all the values in the current query.
   *
   * @param valueClass the [KClass] of an item of the query.
   * @param T the type of the document.
   * @return the [Flow] of a [List] of the document values.
   */
  fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>>
}

/**
 * Returns a [Flow] to all the values in the current query.
 *
 * @param T the type of the document.
 * @return the [Flow] of a [List] of the document values.
 */
inline fun <reified T : Any> Query.asFlow(): Flow<List<T?>> = asFlow(T::class)
