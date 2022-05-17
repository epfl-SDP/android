package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot

/**
 * A decorator which limits the number of results which are to be provided in the resulting results.
 *
 * @param query the query to decorate.
 * @param count the number of items to limit the query to.
 */
class LimitFakeQueryDecorator(
    query: FakeQuery,
    private val count: Long,
) : AbstractFakeQuery(query) {

  override fun FakeQuerySnapshot.transform() = copy(documents = documents.take(count.toInt()))
}
