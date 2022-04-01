package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope

/**
 * A [FakeDocumentRecord] which contains all the fields from a document. Document records do not
 * enforce a specific shape.
 *
 * @param fields the fields of the record.
 */
data class FakeDocumentRecord(val fields: Map<String, Any?>) {

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

/**
 * An implementation of [DocumentEditScope] which records the mutations that were performed on the
 * scope. Mutations which affect the same field will be replaced with their latest value.
 */
private class RecordingDocumentEditScope : DocumentEditScope {

  /** The mutations which have been applied within the [DocumentEditScope]. */
  val mutations: Map<String, Any?>
    get() = recording

  /** The map which records the mutations. */
  private val recording = mutableMapOf<String, Any?>()

  override fun set(field: String, value: Any?) {
    recording[field] = value
  }
}
