package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlin.reflect.KClass

/**
 * An interface representing a view of the results of a [Query]. Snapshots are an abstract type
 * which can then be converted to objects with the actual results of the query.
 */
interface QuerySnapshot {

  /**
   * Maps the [QuerySnapshot] to a [List] of [T].
   *
   * @param T the type of the objects to create with the query results.
   * @param valueClass the [KClass] of [T].
   *
   * @return a [List] of [T], with the results of the query.
   */
  fun <T : Any> toObjects(valueClass: KClass<T>): List<T?>
}
