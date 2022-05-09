package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlin.reflect.KClass

/**
 * A [Transaction] offers a way for clients to perform multiple [get] of documents, followed by some
 * [set] and [update]. At most 500 documents may be modified per [Transaction].
 *
 * Transactions should not have side-effects, as they might be retried if they failed in the first
 * place.
 */
interface Transaction<in D> {

  /**
   * Sets the given [DocumentReference] using the provided [scope].
   *
   * @param reference the [DocumentReference] to set.
   * @param scope the [DocumentEditScope] in which editing operations are taking place. Existing
   * fields will be discarded, and the document will be created if it wasn't present previously.
   */
  fun set(reference: D, scope: DocumentEditScope.() -> Unit)

  /**
   * Sets the given [DocumentReference] with the provided [value].
   *
   * @param T the type of the document.
   * @param reference the [DocumentReference] to set.
   * @param value the value of the document which should be set. Existing fields will be discarded.
   * @param valueClass the [KClass] of the item that is set.
   */
  fun <T : Any> set(reference: D, value: T, valueClass: KClass<T>)

  /**
   * Updates the given [DocumentReference] using the provided [scope].
   *
   * @param reference the [DocumentReference] to update.
   * @param scope the [DocumentEditScope] in which editing operations are taking place. Existing
   * fields will be preserved, and the document will be created if it wasn't present previously.
   */
  fun update(reference: D, scope: DocumentEditScope.() -> Unit)

  /**
   * Deletes the given [DocumentReference].
   *
   * @param reference the [DocumentReference] to remove.
   */
  fun delete(reference: D)

  /**
   * Retrieves the [DocumentSnapshot] for the provided [DocumentReference].
   *
   * @param reference the [DocumentReference] to fetch.
   * @return the [DocumentSnapshot] which was fetched.
   */
  fun getSnapshot(reference: D): DocumentSnapshot
}

/**
 * Sets the given [DocumentReference] with the provided [value].
 *
 * @param T the type of the document.
 * @param reference the [DocumentReference] to set.
 * @param value the value of the document which should be set. Existing fields will be discarded.
 */
inline fun <reified T : Any, D> Transaction<D>.set(
    reference: D,
    value: T,
) = set(reference, value, T::class)

/**
 * Retrieves the [DocumentSnapshot] for the provided [DocumentReference].
 *
 * @param reference the [DocumentReference] to fetch.
 * @param T the type of the document.
 * @return the [T] which was fetched.
 */
inline fun <reified T : Any, D> Transaction<D>.get(
    reference: D,
): T? = getSnapshot(reference).toObject(T::class)
