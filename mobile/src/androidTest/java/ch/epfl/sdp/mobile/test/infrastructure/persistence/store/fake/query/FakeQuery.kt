package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query.WhereFakeQueryDecorator.Filter.where
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query.WhereFakeQueryDecorator.Filter.whereArray
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

  override fun orderBy(field: String, direction: Query.Direction): FakeQuery =
      OrderByQueryDecorator(this, field, direction)

  override fun whereGreaterThan(field: String, value: Any, inclusive: Boolean): FakeQuery =
      WhereFakeQueryDecorator(
          this.whereNotEquals(field, null),
          where(field) {
            val bound = if (inclusive) 0 else 1
            FakeFieldComparator.compare(it, value) >= bound
          },
      )

  override fun whereLessThan(field: String, value: Any, inclusive: Boolean): FakeQuery =
      WhereFakeQueryDecorator(
          this.whereNotEquals(field, null),
          where(field) {
            val bound = if (inclusive) 0 else -1
            FakeFieldComparator.compare(it, value) <= bound
          },
      )

  override fun whereEquals(field: String, value: Any?): FakeQuery =
      WhereFakeQueryDecorator(this, where(field) { it == value })

  override fun whereNotEquals(field: String, value: Any?): FakeQuery =
      WhereFakeQueryDecorator(this, where(field) { it != value })

  override fun whereArrayContains(field: String, value: Any): FakeQuery =
      WhereFakeQueryDecorator(this, whereArray(field) { it.contains(value) })

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot>
}
