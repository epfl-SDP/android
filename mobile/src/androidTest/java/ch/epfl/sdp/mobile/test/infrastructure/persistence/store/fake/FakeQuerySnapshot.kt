package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.QuerySnapshot
import kotlin.reflect.KClass

/**
 * An implementation of [QuerySnapshot] that uses some [FakeDocumentSnapshot].
 *
 * @param documents the [List] of underlying [FakeDocumentSnapshot].
 */
data class FakeQuerySnapshot(
    val documents: List<FakeDocumentSnapshot>,
) : QuerySnapshot {

  override fun <T : Any> toObjects(valueClass: KClass<T>): List<T?> {
    return documents.map { it.toObject(valueClass) }
  }
}
