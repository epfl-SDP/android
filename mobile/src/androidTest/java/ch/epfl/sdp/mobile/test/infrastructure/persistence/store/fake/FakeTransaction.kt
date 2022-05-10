package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentSnapshot
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Transaction
import kotlin.reflect.KClass

/**
 * An implementation of [Transaction] that supports [FakeDocumentReference]. It does not offer true
 * transaction semantics, however these aren't really needed in the context of our unit tests and
 * would be out of scope for the [FakeStore].
 */
class FakeTransaction : Transaction<FakeDocumentReference> {

  override fun set(
      reference: FakeDocumentReference,
      scope: DocumentEditScope.() -> Unit,
  ) = reference.atomicSet(scope)

  override fun <T : Any> set(
      reference: FakeDocumentReference,
      value: T,
      valueClass: KClass<T>,
  ) = reference.atomicSet(value, valueClass)

  override fun update(
      reference: FakeDocumentReference,
      scope: DocumentEditScope.() -> Unit,
  ) = reference.atomicUpdate(scope)

  override fun delete(
      reference: FakeDocumentReference,
  ) = reference.atomicDelete()

  override fun getSnapshot(
      reference: FakeDocumentReference,
  ): DocumentSnapshot = reference.atomicGet()
}
