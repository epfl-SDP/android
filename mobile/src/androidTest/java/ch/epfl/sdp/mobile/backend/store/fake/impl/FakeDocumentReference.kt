package ch.epfl.sdp.mobile.backend.store.fake.impl

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.DocumentEditScope
import ch.epfl.sdp.mobile.backend.store.DocumentReference
import ch.epfl.sdp.mobile.backend.store.fake.CollectionBuilder
import ch.epfl.sdp.mobile.backend.store.fake.DocumentBuilder
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeDocumentReference : DocumentReference, CollectionBuilder {

  val record = MutableStateFlow<FakeDocumentRecord<Any>?>(null)
  private val collections = mutableMapOf<String, FakeCollectionReference>()

  override fun collection(path: String): CollectionReference =
      collections.getOrPut(path) { FakeCollectionReference() }

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<T?> =
      record.map { it?.value } as Flow<T?>

  override suspend fun delete() = record.update { null }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    // TODO : We do not support setting a new, empty document (yet).
    record.update {
      val r = requireNotNull(it) { "Setting on an empty document is not supported yet." }
      r.update(from = r.policy.empty, scope = scope)
    }
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    // TODO : We do not support updating an empty document (yet).
    record.update {
      val r = requireNotNull(it) { "Setting on an empty document is not supported yet." }
      r.update(from = r.value, scope = scope)
    }
  }

  override fun collection(path: String, content: DocumentBuilder.() -> Unit) =
      collections.getOrPut(path) { FakeCollectionReference() }.let(content)
}
