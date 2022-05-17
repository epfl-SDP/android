package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
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

  override fun orderBy(path: FieldPath, direction: Query.Direction): FakeQuery =
      OrderByQueryDecorator(this, path, direction)

  override fun whereGreaterThan(path: FieldPath, value: Any, inclusive: Boolean): FakeQuery =
      WhereFakeQueryDecorator(
          this.whereNotEquals(path, null),
          where(path) {
            val bound = if (inclusive) 0 else 1
            FakeFieldComparator.compare(it, value) >= bound
          },
      )

  override fun whereLessThan(path: FieldPath, value: Any, inclusive: Boolean): FakeQuery =
      WhereFakeQueryDecorator(
          this.whereNotEquals(path, null),
          where(path) {
            val bound = if (inclusive) 0 else -1
            FakeFieldComparator.compare(it, value) <= bound
          },
      )

  override fun whereEquals(path: FieldPath, value: Any?): FakeQuery =
      WhereFakeQueryDecorator(this, where(path) { it == value })

  override fun whereNotEquals(path: FieldPath, value: Any?): FakeQuery =
      WhereFakeQueryDecorator(this, where(path) { it != value })

  override fun whereArrayContains(path: FieldPath, value: Any): FakeQuery =
      WhereFakeQueryDecorator(this, whereArray(path) { it.contains(value) })

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot>

  override suspend fun getQuerySnapshot(): FakeQuerySnapshot
}
