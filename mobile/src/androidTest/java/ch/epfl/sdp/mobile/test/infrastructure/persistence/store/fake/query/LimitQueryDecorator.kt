package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A decorator which limits the number of results which are to be provided in the resulting results.
 *
 * @param query the query to decorate.
 * @param count the number of items to limit the query to.
 */
class LimitQueryDecorator(
    private val query: Query,
    private val count: Long,
) : AbstractQuery() {

  override fun <T : Any> asFlow(valueClass: KClass<T>): Flow<List<T?>> =
      query.asFlow(valueClass).map { it.take(count.toInt()) }
}
