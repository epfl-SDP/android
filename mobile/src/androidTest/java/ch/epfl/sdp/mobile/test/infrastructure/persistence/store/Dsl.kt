@file:Suppress("UNCHECKED_CAST")

package ch.epfl.sdp.mobile.test.infrastructure.persistence.store

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeStore
import kotlin.reflect.KClass

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
   * @param T the type of the added document.
   *
   * @param path the name of the document, unique within the collection.
   * @param value the value of the document which is added.
   * @param valueClass the [KClass] of the newly inserted document.
   * @param content the [CollectionBuilder] when building some inner collections.
   */
  fun <T : Any> document(
      path: String,
      value: T,
      valueClass: KClass<T>,
      content: CollectionBuilder.() -> Unit = {},
  )
}

/**
 * Adds a new document at the given path. If the document already exists, its value will be replaced
 * with the new values.
 *
 * @param T the type of the added document.
 *
 * @param path the name of the document, unique within the collection.
 * @param value the value of the document which is added.
 * @param content the [CollectionBuilder] when building some inner collections.
 */
inline fun <reified T : Any> DocumentBuilder.document(
    path: String,
    value: T,
    noinline content: CollectionBuilder.() -> Unit = {}
): Unit = document(path, value, T::class, content)

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
