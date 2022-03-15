package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldValue
import com.google.firebase.firestore.FieldValue.arrayRemove
import com.google.firebase.firestore.FieldValue.arrayUnion

/**
 * An implementation of [DocumentEditScope] which keeps track of all the updates to apply to a
 * document.
 */
class FirestoreDocumentEditScope : DocumentEditScope {

  private val fields = mutableMapOf<String, Any?>()

  /** All the fields which have been updated in the [DocumentEditScope]. */
  val values: Map<String, Any?> = fields

  override fun set(field: String, value: Any?) {
    fields[field] = value.mapFirestoreFieldValue()
  }
}

/**
 * Maps an optional [Any] which may be a [FieldValue] to a native Firestore
 * [com.google.firebase.firestore.FieldValue] if it corresponds.
 *
 * @receiver an optional [Any] for which we want to map field values.
 * @return the mapped [Any] if it was a [FieldValue].
 */
private fun Any?.mapFirestoreFieldValue(): Any? =
    when (this) {
      is FieldValue.ArrayUnion -> arrayUnion(*values.toTypedArray())
      is FieldValue.ArrayRemove -> arrayRemove(*values.toTypedArray())
      else -> this
    }
