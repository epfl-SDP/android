@file:Suppress("UNCHECKED_CAST")

package ch.epfl.sdp.mobile.backend.store.fake

import ch.epfl.sdp.mobile.backend.store.*
import kotlin.reflect.KClass
import kotlin.reflect.full.instanceParameter
import kotlinx.coroutines.flow.*

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
   * @param document the [Document] which models the data stored.
   * @param content the [CollectionBuilder] when building some inner collections.
   */
  fun <T> document(
      path: String,
      document: Document<T>,
      content: CollectionBuilder.() -> Unit = {},
  )
}

/**
 * An interface representing a [Document] that can be stored in the hierarchy of collections.
 * Documents are created in an empty state, and can be incrementally updated using their [update]
 * function.
 *
 * @param T the type of the representation of the document.
 */
interface Document<T> {

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

private class NonUpdatableDocument(value: Any?) : Document<Any?> {
  override val empty: Any? = value
  override fun Any?.update(fields: Map<String, Any?>): Any? = this
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
) = document(path, NonUpdatableDocument(value), content)

private class DataClassDocument(
    private val factory: () -> Any,
) : Document<Any?> {
  override val empty: Any? = null
  override fun Any?.update(fields: Map<String, Any?>): Any {

    // Create the document if it's missing.
    var from: Any = this ?: factory()

    // Get an instance of the copy() method on data classes.
    val copyMethod = from::class.members.first { it.name == "copy" }

    // For each field, call the copy method to update the argument.
    for ((field, value) in fields) {
      val param = copyMethod.parameters.first { it.name == field }
      val instance = requireNotNull(copyMethod.instanceParameter)

      // Call the method on the current from instance, with the updated param.
      from = requireNotNull(copyMethod.callBy(mapOf(instance to from, param to value)))
    }
    return from
  }
}

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
) = document(path, DataClassDocument(factory), content)

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

  override fun collection(path: String): CollectionReference {
    return root.collection(path)
  }
}

private class CollectionReferenceImpl : CollectionReference, DocumentBuilder {

  val documents = mutableMapOf<String, DocumentReferenceImpl>()

  override fun document(path: String): DocumentReference {
    return documents.getOrPut(path) { DocumentReferenceImpl() }
  }

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>> {
    val flows = documents.values.map { it.current }
    return combine(flows) { it.filterNotNull() } as Flow<List<T?>>
  }

  override fun <T> document(
      path: String,
      document: Document<T>,
      content: CollectionBuilder.() -> Unit
  ) {
    val doc = documents.getOrPut(path) { DocumentReferenceImpl() }
    doc.current.value = document.empty
    doc.policy = document as Document<Any?>
    doc.apply(content)
  }
}

private class RecordingDocumentEditScope : DocumentEditScope {
  val mutations = mutableMapOf<String, Any?>()
  override fun set(field: String, value: Any?) {
    mutations[field] = value
  }
}

private class DocumentReferenceImpl : DocumentReference, CollectionBuilder {

  val current = MutableStateFlow<Any?>(null)
  var policy: Document<Any?> = NonUpdatableDocument(null)
  val collections = mutableMapOf<String, CollectionReferenceImpl>()

  override fun collection(path: String): CollectionReference {
    return collections.getOrPut(path) { CollectionReferenceImpl() }
  }

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<T?> {
    return current.asStateFlow() as Flow<T?>
  }

  override suspend fun delete() {
    current.value = null
  }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    val recorder = RecordingDocumentEditScope().apply(scope)
    current.value = with(policy) { policy.empty.update(recorder.mutations) }
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    val recorder = RecordingDocumentEditScope().apply(scope)
    current.value = with(policy) { current.value.update(recorder.mutations) }
  }

  override fun collection(path: String, content: DocumentBuilder.() -> Unit) {
    return collections.getOrPut(path) { CollectionReferenceImpl() }.let(content)
  }
}
