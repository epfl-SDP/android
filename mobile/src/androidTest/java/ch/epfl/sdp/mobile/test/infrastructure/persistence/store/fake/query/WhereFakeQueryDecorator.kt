package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.query

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeDocumentSnapshot
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake.FakeQuerySnapshot

/**
 * A decorator which keeps documents which match a certain predicate.
 *
 * @param query the query to decorate.
 * @param keep the predicate applied to choose which documents to keep.
 */
class WhereFakeQueryDecorator(
    query: FakeQuery,
    private val keep: (FakeDocumentSnapshot) -> Boolean,
) : AbstractFakeQuery(query) {

  override fun FakeQuerySnapshot.transform() = copy(documents = documents.filter(keep))

  /** A bunch of helper filters for the [WhereFakeQueryDecorator]. */
  companion object Filter {

    /**
     * A filter which preserves only fields where the value matches the [keep] predicate. If the
     * record has an empty document, the value will not be returned.
     *
     * @param path the name of the field we're checking.
     * @param keep a predicate which returns true if the document should be kept.
     */
    fun where(
        path: FieldPath,
        keep: (Any?) -> Boolean,
    ): (FakeDocumentSnapshot) -> Boolean = { it.record != null && keep(it.record.fields[path]) }

    /**
     * A filter which preserves only documents which have an array field whose value matches the
     * [keep] predicate. If the record is an empty document, the value will not be returned.
     *
     * @param path the name of the field we're checking.
     * @param keep a predicate which returns true if the document should be kept.
     */
    fun whereArray(
        path: FieldPath,
        keep: (List<Any?>) -> Boolean,
    ): (FakeDocumentSnapshot) -> Boolean =
        predicate@{
          if (it.record == null) return@predicate false
          val array = it.record.fields[path] as? List<Any?> ?: return@predicate false
          keep(array)
        }
  }
}
