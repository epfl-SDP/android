package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.CollectionBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.DocumentBuilder
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization.fromObject
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeDocumentReference : DocumentReference, CollectionBuilder {

  val record = MutableStateFlow<FakeDocumentRecord?>(null)
  private val collections = mutableMapOf<String, FakeCollectionReference>()

  override fun collection(path: String): CollectionReference =
      collections.getOrPut(path) { FakeCollectionReference() }

  override fun asDocumentSnapshotFlow(): Flow<FakeDocumentSnapshot?> =
      record.map { FakeDocumentSnapshot(it) }

  override suspend fun delete() = record.update { null }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    record.update { FakeDocumentRecord().update(scope) }
  }

  override suspend fun <T : Any> set(value: T, valueClass: KClass<T>) {
    record.update { FakeDocumentRecord.fromObject(value, valueClass) }
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    record.update { (it ?: FakeDocumentRecord()).update(scope) }
  }

  override fun collection(path: String, content: DocumentBuilder.() -> Unit) =
      collections.getOrPut(path) { FakeCollectionReference() }.let(content)
}
