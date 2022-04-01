package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A decorator which limits the number of results which are to be provided in the resulting results.
 *
 * @param query the query to decorate.
 * @param count the number of items to limit the query to.
 */
class LimitFakeQueryDecorator(
    private val query: FakeQuery,
    private val count: Long,
) : FakeQuery {

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot> =
      query.asQuerySnapshotFlow().map { it.copy(documents = it.documents.take(count.toInt())) }
}
