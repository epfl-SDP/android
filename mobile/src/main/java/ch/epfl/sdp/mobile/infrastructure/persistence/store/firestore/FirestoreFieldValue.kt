package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldValue
import com.google.firebase.firestore.FieldValue as ActualFieldValue

/**
 * An object which contains some utility methods to manipulate [FieldValue] and [ActualFieldValue].
 */
object FirestoreFieldValue {

  /**
   * Maps an optional [Any] which may be a [FieldValue] to a native Firestore [ActualFieldValue] if
   * it corresponds.
   *
   * @receiver an optional [Any] for which we want to map field values.
   * @return the mapped [Any] if it was a [FieldValue].
   */
  fun Any?.mapFirestoreFieldValue(): Any? =
      when (this) {
        is FieldValue.ArrayUnion -> ActualFieldValue.arrayUnion(*values.toTypedArray())
        is FieldValue.ArrayRemove -> ActualFieldValue.arrayRemove(*values.toTypedArray())
        else -> this
      }
}
