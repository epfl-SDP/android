package ch.epfl.sdp.mobile.backend.store.fake.impl.query

import ch.epfl.sdp.mobile.backend.store.Query

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
