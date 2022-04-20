package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake.FakeDocumentSnapshot
import ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlin.Comparator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A decorator which orders results according to a certain [FakeFieldComparator] on the resulting
 * document snapshots.
 *
 * @param query the query to decorate.
 * @param field the field to compare.
 * @param order the direction of the comparison.
 */
class OrderByQueryDecorator(
    private val query: FakeQuery,
    private val field: String,
    private val order: Query.Direction,
) : FakeQuery {

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot> {
    val comparator =
        Comparator<FakeDocumentSnapshot> { a, b ->
          FakeFieldComparator.compare(a?.record?.fields?.get(field), b?.record?.fields?.get(field))
        }
    val withOrder = if (order == Query.Direction.Ascending) comparator else comparator.reversed()
    return query.asQuerySnapshotFlow().map {
      it.copy(documents = it.documents.sortedWith(withOrder))
    }
  }
}
