package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlinx.coroutines.flow.Flow

/**
 * An implementation of [Query] which provides some default implementations for all the basic query
 * operators. Each operator is implemented using a dedicated decorator, which will simulate the
 * effects that the operator would have on a real database.
 *
 * Additionally, this interface refines the type of [Query] so it can be easily used to build some
 * chained operators within the fake implementation.
 *
 * @see Query
 */
interface FakeQuery : Query {
  override fun limit(count: Long): FakeQuery = LimitFakeQueryDecorator(this, count)
  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot>
}
