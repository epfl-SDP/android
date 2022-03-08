package ch.epfl.sdp.mobile.backend.store.fake.impl

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.DocumentReference
import ch.epfl.sdp.mobile.backend.store.fake.CollectionBuilder
import ch.epfl.sdp.mobile.backend.store.fake.DocumentBuilder
import ch.epfl.sdp.mobile.backend.store.fake.UpdatePolicy
import ch.epfl.sdp.mobile.backend.store.fake.impl.query.AbstractQuery
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCollectionReference : CollectionReference, DocumentBuilder, AbstractQuery() {

  private val documents = mutableMapOf<String, FakeDocumentReference>()

  override fun document(path: String): DocumentReference {
    return documents.getOrPut(path) { FakeDocumentReference() }
  }

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>> {
    val flows = documents.values.map { it.record.map { rec -> rec?.value } }
    return combine(flows) { it.filterNotNull() } as Flow<List<T?>>
  }

  override fun <T> document(
      path: String,
      updatePolicy: UpdatePolicy<T>,
      content: CollectionBuilder.() -> Unit
  ) {
    val doc = documents.getOrPut(path) { FakeDocumentReference() }
    val rec = FakeDocumentRecord(updatePolicy.empty, updatePolicy) as FakeDocumentRecord<Any>
    doc.record.update { rec }
    doc.apply(content)
  }
}
