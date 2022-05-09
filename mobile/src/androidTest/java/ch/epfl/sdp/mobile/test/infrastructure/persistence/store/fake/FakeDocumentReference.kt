package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.test.getOrPut
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.CollectionBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.DocumentBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization.fromObject
import ch.epfl.sdp.mobile.test.updateAndGetWithValue
import kotlin.reflect.KClass
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeDocumentReference(
    id: FakeDocumentId,
) : DocumentReference<FakeCollectionReference>, CollectionBuilder {

  data class State(
      val id: FakeDocumentId,
      val record: FakeDocumentRecord? = null,
      val collections: PersistentMap<String, FakeCollectionReference> = persistentMapOf(),
  )

  val state = MutableStateFlow(State(id))

  override val id: String
    get() = state.value.id.value

  override fun collection(path: String): FakeCollectionReference {
    return state.updateAndGetWithValue {
      val (col, ref) = it.collections.getOrPut(path) { FakeCollectionReference() }
      it.copy(collections = col) to ref
    }
  }

  /** @see get */
  fun atomicGet(): FakeDocumentSnapshot {
    val value = state.value
    return FakeDocumentSnapshot(value.id, value.record)
  }

  override fun asDocumentSnapshotFlow(): Flow<FakeDocumentSnapshot?> =
      state.map { FakeDocumentSnapshot(it.id, it.record) }

  /** @see delete */
  fun atomicDelete() = state.update { it.copy(record = null) }

  override suspend fun delete() = atomicDelete()

  /** @see est */
  fun atomicSet(scope: DocumentEditScope.() -> Unit) {
    state.update { it.copy(record = FakeDocumentRecord().update(scope)) }
  }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) = atomicSet(scope)

  /** @see set */
  fun <T : Any> atomicSet(value: T, valueClass: KClass<T>) {
    state.update { it.copy(record = FakeDocumentRecord.fromObject(value, valueClass)) }
  }

  override suspend fun <T : Any> set(value: T, valueClass: KClass<T>) = atomicSet(value, valueClass)

  /** @see update */
  fun atomicUpdate(scope: DocumentEditScope.() -> Unit) {
    state.update { it.copy(record = (it.record ?: FakeDocumentRecord()).update(scope)) }
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) = atomicUpdate(scope)

  override fun collection(path: String, content: DocumentBuilder.() -> Unit) =
      state
          .updateAndGetWithValue {
            val (col, ref) = it.collections.getOrPut(path) { FakeCollectionReference() }
            it.copy(collections = col) to ref
          }
          .let(content)
}
