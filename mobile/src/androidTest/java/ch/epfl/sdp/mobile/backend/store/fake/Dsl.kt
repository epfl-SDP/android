package ch.epfl.sdp.mobile.backend.store.fake

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.DocumentReference
import ch.epfl.sdp.mobile.backend.store.Store
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * Builds a [Store] using the provided [CollectionBuilder]
 *
 * @param content the builder for the store.
 * @return the newly built fake store.
 */
fun buildFakeStore(content: CollectionBuilder.() -> Unit): Store {
  val impl = StoreImpl()
  impl.root.apply(content)
  return impl
}

/** An interface which defines how a collection of documents is built. */
interface DocumentBuilder {

  /**
   * Adds a new document at the given path. If the document already exists, its value will be
   * replaced with the new values.
   *
   * @param path the name of the document, unique within the collection.
   * @param values the [Flow] of values which will be emitted when reading this document.
   * @param content the [CollectionBuilder] when building some inner collections.
   */
  fun document(
      path: String,
      values: Flow<Any?> = flowOf(null),
      content: CollectionBuilder.() -> Unit = {},
  )
}

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

// Implementation.

private class StoreImpl : Store {

  val root = DocumentReferenceImpl()

  override fun collection(path: String): CollectionReference = root.collection(path)
}

private class CollectionReferenceImpl : CollectionReference, DocumentBuilder {

  val documents = mutableMapOf<String, DocumentReferenceImpl>()

  override fun document(
      path: String,
      values: Flow<Any?>,
      content: CollectionBuilder.() -> Unit,
  ) {
    val document = documents.getOrPut(path) { DocumentReferenceImpl() }
    document.values = values
    document.apply(content)
  }

  override fun document(path: String): DocumentReference {
    return documents.getOrPut(path) { DocumentReferenceImpl() }
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>> {
    val flows = documents.values.map { it.values }
    return combine(flows) { it.toList() } as Flow<List<T?>>
  }
}

private class DocumentReferenceImpl : DocumentReference, CollectionBuilder {

  var values: Flow<Any?> = flowOf(null) // By default, no document is present.
  val collections = mutableMapOf<String, CollectionReferenceImpl>()

  override fun collection(
      path: String,
      content: DocumentBuilder.() -> Unit,
  ): Unit = collections.getOrPut(path) { CollectionReferenceImpl() }.let(content)

  override fun collection(path: String): CollectionReference =
      collections.getOrPut(path) { CollectionReferenceImpl() }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<T?> = values as Flow<T?>
}
