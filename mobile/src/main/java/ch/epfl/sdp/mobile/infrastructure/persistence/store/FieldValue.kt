package ch.epfl.sdp.mobile.infrastructure.persistence.store

/**
 * An interface defining some sentinel values which may be used within [DocumentEditScope] to
 * perform some special operations on fields.
 */
sealed interface FieldValue {

  /**
   * A sentinel value which performs array union on a field.
   *
   * @param values the values which are added to the array.
   */
  data class ArrayUnion(val values: List<Any>) : FieldValue

  /**
   * A sentinel value which performs array removal on a field.
   *
   * @param values the values which are removed from the array.
   */
  data class ArrayRemove(val values: List<Any>) : FieldValue
}
