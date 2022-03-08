package ch.epfl.sdp.mobile.backend.store

import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

/**
 * An interface representing a collection in the hierarchy. It may contain some documents, and can
 * be observed as a [Flow] of changing values.
 */
interface CollectionReference {

  /**
   * Accesses a document.
   *
   * @param path the id of the document.
   * @return a [DocumentReference] to the document.
   */
  fun document(path: String): DocumentReference

  /**
   * Returns a [Flow] to all the values in the current collection.
   *
   * @param valueClass the [KClass] of an item of the collection.
   * @param T the type of the document.
   * @return the [Flow] of a [List] of the document values.
   */
  fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>>
}

/**
 * Returns a [Flow] to all the values in the current collection.
 *
 * @param T the type of the document.
 * @return the [Flow] of a [List] of the document values.
 */
inline fun <reified T : Any> CollectionReference.asFlow(): Flow<List<T?>> = asFlow(T::class)
