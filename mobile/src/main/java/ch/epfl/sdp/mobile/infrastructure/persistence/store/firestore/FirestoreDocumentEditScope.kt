package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope

/**
 * An implementation of [DocumentEditScope] which keeps track of all the updates to apply to a
 * document.
 */
class FirestoreDocumentEditScope : DocumentEditScope {

  private val fields = mutableMapOf<String, Any?>()

  /** All the fields which have been updated in the [DocumentEditScope]. */
  val values: Map<String, Any?> = fields

  override fun set(field: String, value: Any?) {
    fields[field] = value
  }
}
