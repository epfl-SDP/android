@file:Suppress("UNCHECKED_CAST")

package ch.epfl.sdp.mobile.backend.store.fake

import ch.epfl.sdp.mobile.backend.store.*
import ch.epfl.sdp.mobile.backend.store.fake.impl.FakeStore
import ch.epfl.sdp.mobile.backend.store.fake.impl.documents.DataClassUpdatePolicy
import ch.epfl.sdp.mobile.backend.store.fake.impl.documents.NoUpdatePolicy

/** Builds and returns a [Store] with no data. */
fun emptyStore(): Store = FakeStore()

/**
 * Builds a [Store] using the provided [CollectionBuilder]
 *
 * @param content the builder for the store.
 * @return the newly built fake store.
 */
fun buildStore(
    content: CollectionBuilder.() -> Unit,
): Store = FakeStore().apply(content)

/** An interface which defines how a collection of documents is built. */
interface DocumentBuilder {

  /**
   * Adds a new document at the given path. If the document already exists, its value will be
   * replaced with the new values.
   *
   * @param path the name of the document, unique within the collection.
   * @param updatePolicy the [UpdatePolicy] which models the data stored.
   * @param content the [CollectionBuilder] when building some inner collections.
   */
  fun <T> document(
      path: String,
      updatePolicy: UpdatePolicy<T>,
      content: CollectionBuilder.() -> Unit = {},
  )
}

/**
 * An interface representing an [UpdatePolicy] that can be stored in the hierarchy of collections.
 * Documents are created in an empty state, and can be incrementally updated using the [update]
 * function of the policy.
 *
 * @param T the type of the representation of the document.
 */
interface UpdatePolicy<T> {

  /** Creates an empty document representation. */
  val empty: T

  /**
   * Updates the given document representation with the given [fields]. Once updated, a new
   * representation must be returned, otherwise the document changes won't be observable.
   *
   * @param fields the fields which have been updated in the document.
   *
   * @return the new document, with the updated fields.
   */
  fun T.update(fields: Map<String, Any?>): T
}

/**
 * Adds a new document at the given path. If the document already exists, its value will be replaced
 * with the new values.
 *
 * This document will not support updates using the [DocumentReference.update] or
 * [DocumentReference.set] methods.
 *
 * @param path the name of the document, unique within the collection.
 * @param value the value which will be emitted when reading this document.
 * @param content the [CollectionBuilder] when building some inner collections.
 */
fun DocumentBuilder.document(
    path: String,
    value: Any?,
    content: CollectionBuilder.() -> Unit = {},
) = document(path, NoUpdatePolicy(value), content)

/**
 * Adds a new document, backed by a data class, at the given path. If the document already exists,
 * its value will be replaced with the new values.
 *
 * This document will support updates, but won't check the types of the updated values, so you
 * should make sure that it matches the expected types.
 *
 * @param path the name of the document, unique within the collection.
 * @param factory a factory function to create a new, empty data class of the given type.
 * @param content the [CollectionBuilder] when building some inner collections.
 */
fun <T : Any> DocumentBuilder.dataclassDocument(
    path: String,
    factory: () -> T,
    content: CollectionBuilder.() -> Unit = {},
) = document(path, DataClassUpdatePolicy(factory), content)

/** An interface which defines how a set of collection is built. */
interface CollectionBuilder {

  /**
   * Adds a new collection at the given path. If the collection already exists, its value will be
   * merged with existing collection.
   *
   * @param path the name of the collection, unique within this level of the hierarchy.
   * @param content the [DocumentBuilder] when building some inner documents.
   */
  fun collection(
      path: String,
      content: DocumentBuilder.() -> Unit,
  )
}
