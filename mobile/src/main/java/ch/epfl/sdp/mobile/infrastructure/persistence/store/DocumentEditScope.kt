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
