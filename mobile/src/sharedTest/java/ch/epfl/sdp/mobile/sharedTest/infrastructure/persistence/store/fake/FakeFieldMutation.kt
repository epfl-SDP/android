package ch.epfl.sdp.mobile.sharedTest.infrastructure.persistence.store.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldValue

/**
 * An interface representing the different kinds of mutations which may be applied to a field.
 *
 * @param field the name of the field to which the mutation is applied.
 */
sealed class FakeFieldMutation(val field: String) {

  /**
   * Mutates the existing value and transforms it through this mutation.
   *
   * @param value the existing value.
   * @return the mutated result.
   */
  abstract fun mutate(value: Any?): Any?

  companion object Factory {

    /**
     * Creates a [FakeFieldMutation] for the given [field] and the provided [value].
     *
     * @param field the field for which we're creating the mutation.
     * @param value the value which was set.
     */
    fun from(field: String, value: Any?): FakeFieldMutation =
        when (value) {
          is FieldValue.ArrayUnion -> ArrayUnion(field, value.values)
          is FieldValue.ArrayRemove -> ArrayRemove(field, value.values)
          else -> Set(field, value)
        }
  }
}

private class Set(field: String, private val to: Any?) : FakeFieldMutation(field) {
  override fun mutate(value: Any?): Any? = to
}

private class ArrayUnion(field: String, private val values: List<Any>) : FakeFieldMutation(field) {
  override fun mutate(value: Any?): Any {
    val existing = value as? List<Any?> ?: emptyList()
    return existing + values.minus(existing)
  }
}

private class ArrayRemove(field: String, private val values: List<Any>) : FakeFieldMutation(field) {
  override fun mutate(value: Any?): Any {
    val existing = value as? List<Any?> ?: emptyList()
    return existing.minus(values)
  }
}
