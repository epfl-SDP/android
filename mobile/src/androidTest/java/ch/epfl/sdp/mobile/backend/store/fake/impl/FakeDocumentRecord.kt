package ch.epfl.sdp.mobile.backend.store.fake.impl

import ch.epfl.sdp.mobile.backend.store.DocumentEditScope
import ch.epfl.sdp.mobile.backend.store.fake.UpdatePolicy

/**
 * A class representing the current state of a document. Documents are created using a certain
 * [UpdatePolicy], and a [value] which represents the current state of the document.
 *
 * @param T the type of the document values.
 */
data class FakeDocumentRecord<T>(
    val value: T,
    val policy: UpdatePolicy<T>,
) {

  /**
   * Updates the [FakeDocumentRecord] using the given [scope], starting from the given value. This
   * returns a new immutable.
   *
   * @param from the value from which the update was performed, which defaults to the current record
   * value.
   * @param scope the [DocumentEditScope] lambda that should be applied.
   */
  fun update(
      from: T = value,
      scope: DocumentEditScope.() -> Unit,
  ): FakeDocumentRecord<T> {
    val mutations = RecordingDocumentEditScope().apply(scope).mutations
    return copy(value = with(policy) { from.update(mutations) })
  }
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
