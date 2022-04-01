package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.sharedTest.getOrPut
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.CollectionBuilder
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.DocumentBuilder
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake.serialization.fromObject
import ch.epfl.sdp.mobile.sharedTest.updateAndGetWithValue
import kotlin.reflect.KClass
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.*

class FakeDocumentReference(id: FakeDocumentId) : DocumentReference, CollectionBuilder {

  data class State(
      val id: FakeDocumentId,
      val record: FakeDocumentRecord? = null,
      val collections: PersistentMap<String, FakeCollectionReference> = persistentMapOf(),
  )

  val state = MutableStateFlow(State(id))

  override fun collection(path: String): CollectionReference {
    return state.updateAndGetWithValue {
      val (col, ref) = it.collections.getOrPut(path) { FakeCollectionReference() }
      it.copy(collections = col) to ref
    }
  }

  override fun asDocumentSnapshotFlow(): Flow<FakeDocumentSnapshot?> =
      state.map { FakeDocumentSnapshot(it.id, it.record) }

  override suspend fun delete() = state.update { it.copy(record = null) }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    state.update { it.copy(record = FakeDocumentRecord().update(scope)) }
  }

  override suspend fun <T : Any> set(value: T, valueClass: KClass<T>) {
    state.update { it.copy(record = FakeDocumentRecord.fromObject(value, valueClass)) }
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    state.update { it.copy(record = (it.record ?: FakeDocumentRecord()).update(scope)) }
  }

  override fun collection(path: String, content: DocumentBuilder.() -> Unit) =
      state
          .updateAndGetWithValue {
            val (col, ref) = it.collections.getOrPut(path) { FakeCollectionReference() }
            it.copy(collections = col) to ref
          }
          .let(content)
}
