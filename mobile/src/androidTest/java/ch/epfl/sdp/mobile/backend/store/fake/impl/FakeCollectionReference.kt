package ch.epfl.sdp.mobile.backend.store.fake.impl

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.DocumentReference
import ch.epfl.sdp.mobile.backend.store.fake.CollectionBuilder
import ch.epfl.sdp.mobile.backend.store.fake.DocumentBuilder
import ch.epfl.sdp.mobile.backend.store.fake.impl.query.AbstractQuery
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class FakeCollectionReference : CollectionReference, DocumentBuilder, AbstractQuery() {

  private val documents = mutableMapOf<String, FakeDocumentReference>()

  override fun document(path: String): DocumentReference {
    return documents.getOrPut(path) { FakeDocumentReference() }
  }

  // Casting is needed because combine(...) is inline <reified T>, and there is no KClass-based
  // implementation of combine(...).
  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>> {
    val flows = documents.values.map { it.asFlow(valueClass) } as List<Flow<*>>
    return combine(flows) { it.filterNotNull() } as Flow<List<T?>>
  }

  override fun <T : Any> document(
      path: String,
      value: T,
      valueClass: KClass<T>,
      content: CollectionBuilder.() -> Unit
  ) {
    val doc = documents.getOrPut(path) { FakeDocumentReference() }
    val rec = FakeDocumentRecord.fromObject(value, valueClass)
    doc.record.update { rec }
    doc.apply(content)
  }
}
