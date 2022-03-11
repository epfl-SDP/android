package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query

/**
 * An implementation of [Query] which provides some default implementations for all the basic query
 * operators. Each operator is implemented using a dedicated decorator, which will simulate the
 * effects that the operator would have on a real database.
 *
 * @see Query
 */
abstract class AbstractQuery : Query {
  override fun limit(count: Long): Query = LimitQueryDecorator(this, count)
}
