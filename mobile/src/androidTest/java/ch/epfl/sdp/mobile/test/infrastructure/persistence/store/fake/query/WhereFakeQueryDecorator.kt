package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentSnapshot
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * A decorator which keeps documents which match a certain predicate.
 *
 * @param query the query to decorate.
 * @param keep the predicate applied to choose which documents to keep.
 */
class WhereFakeQueryDecorator(
    private val query: FakeQuery,
    private val keep: (FakeDocumentSnapshot) -> Boolean,
) : FakeQuery {

  override fun asQuerySnapshotFlow(): Flow<FakeQuerySnapshot> =
      query.asQuerySnapshotFlow().map { it.copy(documents = it.documents.filter(keep)) }

  override fun startsWith(field: String, prefix: String): Query {
    TODO("Not yet implemented")
  }

  /** A bunch of helper filters for the [WhereFakeQueryDecorator]. */
  companion object Filter {

    /**
     * A filter which preserves only fields where the value matches the [keep] predicate. If the
     * record has an empty document, the value will not be returned.
     *
     * @param field the name of the field we're checking.
     * @param keep a predicate which returns true if the document should be kept.
     */
    fun where(
        field: String,
        keep: (Any?) -> Boolean,
    ): (FakeDocumentSnapshot) -> Boolean = { it.record != null && keep(it.record.fields[field]) }

    /**
     * A filter which preserves only documents which have an array field whose value matches the
     * [keep] predicate. If the record is an empty document, the value will not be returned.
     *
     * @param field the name of the field we're checking.
     * @param keep a predicate which returns true if the document should be kept.
     */
    fun whereArray(
        field: String,
        keep: (List<Any?>) -> Boolean,
    ): (FakeDocumentSnapshot) -> Boolean =
        predicate@{
          if (it.record == null) return@predicate false
          val array = it.record.fields[field] as? List<Any?> ?: return@predicate false
          keep(array)
        }
  }
}
