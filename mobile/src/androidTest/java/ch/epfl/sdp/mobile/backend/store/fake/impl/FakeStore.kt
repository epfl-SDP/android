package ch.epfl.sdp.mobile.backend.store.fake.impl

import ch.epfl.sdp.mobile.backend.store.CollectionReference
import ch.epfl.sdp.mobile.backend.store.Store
import ch.epfl.sdp.mobile.backend.store.fake.CollectionBuilder

/**
 * An implementation of a [Store] which delegates its implementation to an inner fake document. This
 * document will not have any value, however, it can be used to build some nested collections.
 *
 * @param root the [FakeDocumentReference] which is the root of the hierarchy.
 */
class FakeStore
private constructor(
    private val root: FakeDocumentReference,
) : Store, CollectionBuilder by root {

  /** A convenience constructor, which creates an empty [FakeStore]. */
  constructor() : this(FakeDocumentReference())

  override fun collection(path: String): CollectionReference = root.collection(path)
}
