package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import ch.epfl.sdp.mobile.infrastructure.persistence.store.RecordingDocumentEditScope

/**
 * A [FakeDocumentRecord] which contains all the fields from a document. Document records do not
 * enforce a specific shape.
 *
 * @param fields the fields of the record.
 */
data class FakeDocumentRecord(val fields: Map<FieldPath, Any?>) {

  /** A convenience construction which builds an empty [FakeDocumentRecord]. */
  constructor() : this(emptyMap())

  /**
   * Updates the [FakeDocumentRecord] using the given [scope], starting from the given value. This
   * returns a new immutable record.
   *
   * @param scope the [DocumentEditScope] lambda which should be applied.
   */
  fun update(scope: DocumentEditScope.() -> Unit): FakeDocumentRecord {
    val recorded = RecordingDocumentEditScope().apply(scope)
    val mutations = recorded.mutations.map { (f, v) -> FakeFieldMutation.from(f, v) }
    val updated = mutations.fold(fields) { acc, m -> acc + (m.field to m.mutate(acc[m.field])) }
    return copy(fields = updated)
  }

  companion object
}
