package ch.epfl.sdp.mobile.infrastructure.persistence.store

/**
 * An implementation of [DocumentEditScope] which records the mutations that were performed on the
 * scope. Mutations which affect the same field will be replaced with their latest value.
 */
class RecordingDocumentEditScope : DocumentEditScope {

  /** The mutations which have been applied within the [DocumentEditScope]. */
  val mutations: Map<FieldPath, Any?>
    get() = recording

  /** The map which records the mutations. */
  private val recording = mutableMapOf<FieldPath, Any?>()

  override fun set(path: FieldPath, value: Any?) {
    // Flatten the nested Map<String, Any?>.
    val items = mutableListOf(path to value)
    while (items.isNotEmpty()) {
      val (p, v) = items.removeLast()
      if (v is Map<*, *>) {
        for ((km, vm) in v) items.add(p + km.toString() to vm)
      } else recording[p] = v
    }
  }
}
