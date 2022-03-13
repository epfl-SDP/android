package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlin.reflect.KClass

/**
 * An interface representing a view of the results of fetching a [DocumentReference]. Snapshots are
 * an abstract type which can be converted to an object with the actual result.
 */
interface DocumentSnapshot {

  /**
   * Maps the [DocumentSnapshot] to an optional [T].
   *
   * @param T the type of the object to create with the fetch result.
   * @param valueClass the [KClass] of [T].
   *
   * @return a [T] with the result of the fetch.
   */
  fun <T : Any> toObject(valueClass: KClass<T>): T?
}
