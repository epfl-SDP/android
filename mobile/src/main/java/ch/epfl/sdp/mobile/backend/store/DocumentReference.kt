package ch.epfl.sdp.mobile.backend.store

import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

/**
 * An interface representing a document in the hierarchy. It may contain some nested collections,
 * and can be observed as a [Flow] of changing values.
 */
interface DocumentReference {

  /**
   * Accesses a nested collection.
   *
   * @param path the id of the nested collection path.
   * @return a [CollectionReference] to the nested collection.
   */
  fun collection(path: String): CollectionReference

  /**
   * Returns a [Flow] of the current value of the document.
   *
   * @param valueClass the [KClass] of the item.
   * @param T the type of the document.
   * @return the [Flow] of the document values.
   */
  fun <T : Any> asFlow(valueClass: KClass<T>): Flow<T?>
}

/**
 * Returns a [Flow] of the current value of the document.
 *
 * @param T the type of the document.
 * @return the [Flow] of the document values.
 */
inline fun <reified T : Any> DocumentReference.asFlow(): Flow<T?> = asFlow(T::class)
