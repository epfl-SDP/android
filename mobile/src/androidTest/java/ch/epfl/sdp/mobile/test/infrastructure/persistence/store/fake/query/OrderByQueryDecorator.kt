package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query.Direction.Ascending
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentSnapshot
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlin.Comparator

/**
 * A decorator which orders results according to a certain [FakeFieldComparator] on the resulting
 * document snapshots.
 *
 * @param query the query to decorate.
 * @param path the field to compare.
 * @param order the direction of the comparison.
 */
class OrderByQueryDecorator(
    query: FakeQuery,
    path: FieldPath,
    order: Query.Direction,
) : AbstractFakeQuery(query) {

  private val comparator =
      Comparator<FakeDocumentSnapshot> { a, b ->
        FakeFieldComparator.compare(
            a?.record?.fields?.get(path),
            b?.record?.fields?.get(path),
        )
      }

  private val withOrder = if (order == Ascending) comparator else comparator.reversed()

  override fun FakeQuerySnapshot.transform() = copy(documents = documents.sortedWith(withOrder))
}
