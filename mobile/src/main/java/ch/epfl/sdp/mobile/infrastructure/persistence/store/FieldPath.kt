package ch.epfl.sdp.mobile.infrastructure.persistence.store

/**
 * A [FieldPath] represents the path of a field within a document. Each field is uniquely identified
 * by a non-empty path.
 *
 * @property segments the segments of the path within this document.
 */
data class FieldPath(val segments: List<String>) {

  /**
   * A convenience constructor which builds a [FieldPath] from a single [String] segment.
   *
   * @param segment the unique segment of this [FieldPath].
   */
  constructor(segment: String) : this(listOf(segment))

  /**
   * Concatenates the given [segment] to the [FieldPath].
   *
   * @return an updated [FieldPath] with the new segment.
   */
  operator fun plus(segment: String): FieldPath = FieldPath(segments + segment)

  init {
    require(segments.isNotEmpty()) { "A FieldPath may not have 0 segments." }
  }
}
