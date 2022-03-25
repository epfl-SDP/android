package ch.epfl.sdp.mobile.infrastructure.persistence.store

import kotlinx.coroutines.flow.Flow

/**
 * An interface representing a collection in the hierarchy. It may contain some documents, and can
 * be observed as a [Flow] of changing values.
 */
interface CollectionReference : Query {

  /**
   * Creates a document with a unique identifier.
   *
   * @return a [DocumentReference] to the document.
   */
  fun document(): DocumentReference

  /**
   * Accesses a document.
   *
   * @param path the id of the document.
   * @return a [DocumentReference] to the document.
   */
  fun document(path: String): DocumentReference
}
