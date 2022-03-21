package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
   * Returns a [Flow] of all the snapshots for the current [DocumentReference].
   *
   * @return the [Flow] of [DocumentSnapshot].
   */
  fun asDocumentSnapshotFlow(): Flow<DocumentSnapshot?>

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
   * Sets the given document with the provided [value].
   *
   * @param T the type of the document.
   * @param value hte value of the document which should be set. Existing fields will be discarded.
   * @param valueClass the [KClass] of the item that is set.
   */
  suspend fun <T : Any> set(value: T, valueClass: KClass<T>)

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
inline fun <reified T : Any> DocumentReference.asFlow(): Flow<T?> =
    asDocumentSnapshotFlow().map { it?.toObject(T::class) }

/**
 * Sets the given document with the provided [value].
 *
 * @param T the type of the document.
 * @param value the value of the document which should be set. Existing fields will be discarded.
 */
suspend inline fun <reified T : Any> DocumentReference.set(value: T): Unit = set(value, T::class)

/**
 * Sets the given document with the provided [values].
 *
 * @receiver the [DocumentReference] onto which the items will be set.
 * @param values the [Map] of the values which will be set to the document.
 */
suspend fun DocumentReference.set(values: Map<String, Any?>) = set {
  for ((key, value) in values) {
    this[key] = value
  }
}
