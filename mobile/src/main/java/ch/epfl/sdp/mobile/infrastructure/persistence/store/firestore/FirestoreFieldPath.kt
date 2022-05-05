package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.FieldPath
import com.google.firebase.firestore.FieldPath as ActualFieldPath
import com.google.firebase.firestore.FieldPath.of

/**
 * An object which contains some utility methods to manipulate [FieldPath] and [ActualFieldPath].
 */
object FirestoreFieldPath {

  /** Transforms the given [FieldPath] to an [ActualFieldPath], using the individual segments. */
  fun FieldPath.toFirestoreFieldPath(): ActualFieldPath = of(*segments.toTypedArray())
}
