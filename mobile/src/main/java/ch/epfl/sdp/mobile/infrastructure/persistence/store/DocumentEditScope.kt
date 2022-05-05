package ch.epfl.sdp.mobile.infrastructure.persistence.store

/** An interface which defines edition operations on a document. */
interface DocumentEditScope {

  /**
   * Sets the given [path] tto the given [value] in the document.
   *
   * @param path the path to edit.
   * @param value the value to set the field to.
   */
  operator fun set(path: FieldPath, value: Any?)

  /**
   * Sets the given [field] to the given [value] in the document.
   *
   * @param field the field to edit.
   * @param value the value to set the field to.
   */
  operator fun set(field: String, value: Any?) = set(FieldPath(field), value)
}

/**
 * A variation of [set] which performs the union of the values of an array with field path and the
 * provided [values]. If the field being modified is not already an array, an array with the
 * specified elements will be created.
 *
 * @param path the path to edit.
 * @param values the values to add to the array at this field.
 */
fun DocumentEditScope.arrayUnion(
    path: FieldPath,
    vararg values: Any,
): Unit = set(path, FieldValue.ArrayUnion(values.toList()))

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
): Unit = arrayUnion(FieldPath(field), *values)

/**
 * A variation of [set] which performs a subtraction of the values of an array with the field path
 * and the provided [values]. If the field being modified is not already an array, an empty array
 * will be created.
 *
 * @param path the path to edit.
 * @param values the values to remove from the array at this field.
 */
fun DocumentEditScope.arrayRemove(
    path: FieldPath,
    vararg values: Any,
): Unit = set(path, FieldValue.ArrayRemove(values.toList()))

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
): Unit = arrayRemove(FieldPath(field), *values)
