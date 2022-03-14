package ch.epfl.sdp.mobile.infrastructure.persistence.store

/** An interface which defines edition operations on a document. */
interface DocumentEditScope {

  /**
   * Sets the given [field] to the given [value] in the document.
   *
   * @param field the field to edit.
   * @param value the value to set the field to.
   */
  operator fun set(field: String, value: Any?)
}

/**
 * A variation of [set] which performs the union of the values of an array with key [field] and the
 * provided [values]. If the field being modified is not already an array, an array with the
 * specified elements will be created.
 *
 * @param field the field to edit.
 * @param values the values to add to the array at this field.
 */
fun DocumentEditScope.arrayUnion(
    field: String,
    vararg values: Any,
): Unit = set(field, FieldValue.ArrayUnion(values.toList()))

/**
 * A variation of [set] which performs a subtraction of the values of an array with the key [field]
 * and the provided [values]. If the field being modified is not already an array, an empty array
 * will be created.
 *
 * @param field the field to edit.
 * @param values the values to remove from the array at this field.
 */
fun DocumentEditScope.arrayRemove(
    field: String,
    vararg values: Any,
): Unit = set(field, FieldValue.ArrayRemove(values.toList()))
