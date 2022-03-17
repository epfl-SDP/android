package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.CollectionBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.DocumentBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query.FakeQuery
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization.fromObject
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class FakeCollectionReference : CollectionReference, DocumentBuilder, FakeQuery {

  private val documents = mutableMapOf<String, FakeDocumentReference>()

  override fun document(path: String): DocumentReference {
    return documents.getOrPut(path) { FakeDocumentReference() }
  }

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot> {
    val flows = documents.values.map { it.asDocumentSnapshotFlow() }
    return combine(flows) { FakeQuerySnapshot(it.filterNotNull()) }
  }

  override fun startsWith(field: String, prefix: String): Query {
    TODO("Not yet implemented")
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
