package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.QuerySnapshot
import com.google.firebase.firestore.QuerySnapshot as ActualQuerySnapshot
import kotlin.reflect.KClass

/**
 * An implementation of [QuerySnapshot] which uses an [ActualQuerySnapshot] under-the-hood.
 *
 * @property actual the actual [ActualQuerySnapshot].
 */
class FirestoreQuerySnapshot(
    private val actual: ActualQuerySnapshot,
) : QuerySnapshot {

  override fun <T : Any> toObjects(valueClass: KClass<T>): List<T?> =
      actual.toObjects(valueClass.java)
}
