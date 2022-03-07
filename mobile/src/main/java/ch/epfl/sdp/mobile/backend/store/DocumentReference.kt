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

  /** Deletes the given document. Does nothing if the document was previously missing. */
  suspend fun delete()

  /**
   * Sets the given document with the [scope].
   *
   * @param scope the [DocumentEditScope] in which editing operations are taking place. Existing
   * fields will be discarded, and the document will be created if it wasn't present previously.
   */
  suspend fun set(scope: DocumentEditScope.() -> Unit)

  /**
   * Updates the given document within the [scope].
   *
   * @param scope the [DocumentEditScope] in which editing operations are taking place. Existing
   * fields will be preserved, and the document will be created if it wasn't present previously.
   */
  suspend fun update(scope: DocumentEditScope.() -> Unit)
}

/**
 * Returns a [Flow] of the current value of the document.
 *
 * @param T the type of the document.
 * @return the [Flow] of the document values.
 */
inline fun <reified T : Any> DocumentReference.asFlow(): Flow<T?> = asFlow(T::class)
