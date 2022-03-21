package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.test.combineOrEmpty
import ch.epfl.sdp.mobile.test.getOrPut
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.CollectionBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.DocumentBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query.FakeQuery
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization.fromObject
import ch.epfl.sdp.mobile.test.updateAndGetWithValue
import kotlin.reflect.KClass
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCollectionReference : CollectionReference, DocumentBuilder, FakeQuery {

  data class State(
      val documents: PersistentMap<String, FakeDocumentReference> = persistentMapOf(),
  )

  private val state = MutableStateFlow(State())

  override fun document(path: String): FakeDocumentReference {
    return state.updateAndGetWithValue {
      val (doc, ref) = it.documents.getOrPut(path) { FakeDocumentReference(FakeDocumentId(path)) }
      it.copy(documents = doc) to ref
    }
  }

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot> {
    return state.flatMapLatest {
      val flows = it.documents.values.map { doc -> doc.asDocumentSnapshotFlow() }
      flows.combineOrEmpty().map { snapshots -> FakeQuerySnapshot(snapshots.filterNotNull()) }
    }
  }

  override fun <T : Any> document(
      path: String,
      value: T,
      valueClass: KClass<T>,
      content: CollectionBuilder.() -> Unit
  ) {
    val doc = document(path)
    val rec = FakeDocumentRecord.fromObject(value, valueClass)
    doc.state.update { it.copy(record = rec) }
    doc.apply(content)
  }
}
