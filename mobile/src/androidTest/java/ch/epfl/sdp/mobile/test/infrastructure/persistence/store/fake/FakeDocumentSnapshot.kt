package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentSnapshot
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.serialization.toObject
import kotlin.reflect.KClass

/**
 * A [DocumentSnapshot] which wraps a [FakeDocumentRecord].
 *
 * @param record the (optional) [FakeDocumentRecord] which the snapshot wraps.
 */
data class FakeDocumentSnapshot(
    val record: FakeDocumentRecord?,
) : DocumentSnapshot {
  override fun <T : Any> toObject(valueClass: KClass<T>): T? = record?.toObject(valueClass)
}
