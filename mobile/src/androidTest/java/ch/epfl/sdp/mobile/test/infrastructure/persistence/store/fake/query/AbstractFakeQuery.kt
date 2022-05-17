package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlinx.coroutines.flow.map

/**
 * An abstract implementation of [FakeQuery] which delegates all the transformation behaviour to its
 * [FakeQuerySnapshot.transform] function, decorating an underlying [FakeQuery].
 *
 * @param parent the underlying [FakeQuery].
 */
abstract class AbstractFakeQuery(private val parent: FakeQuery) : FakeQuery {

  /**
   * Transforms the [FakeQuerySnapshot] using this query.
   *
   * @receiver the original [FakeQuerySnapshot].
   * @return the transformed [FakeQuerySnapshot].
   */
  abstract fun FakeQuerySnapshot.transform(): FakeQuerySnapshot

  override fun asQuerySnapshotFlow() = parent.asQuerySnapshotFlow().map { it.transform() }

  override suspend fun getQuerySnapshot() = parent.getQuerySnapshot().transform()
}
