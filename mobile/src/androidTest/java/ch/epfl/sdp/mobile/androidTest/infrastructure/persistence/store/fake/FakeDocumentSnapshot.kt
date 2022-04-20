package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentSnapshot
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.fake.serialization.toObject
import kotlin.reflect.KClass

/**
 * A [DocumentSnapshot] which wraps a [FakeDocumentRecord].
 *
 * @param id the document identifier.
 * @param record the (optional) [FakeDocumentRecord] which the snapshot wraps.
 */
data class FakeDocumentSnapshot(
    val id: FakeDocumentId,
    val record: FakeDocumentRecord?,
) : DocumentSnapshot {
  override fun <T : Any> toObject(valueClass: KClass<T>): T? = record?.toObject(id, valueClass)
}
