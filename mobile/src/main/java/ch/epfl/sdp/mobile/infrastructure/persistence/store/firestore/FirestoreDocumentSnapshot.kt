package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentSnapshot
import com.google.firebase.firestore.DocumentSnapshot as ActualDocumentSnapshot
import kotlin.reflect.KClass

/**
 * An implementation of [DocumentSnapshot] which uses an [ActualDocumentSnapshot] under-the-hood.
 *
 * @param actual the actual [ActualDocumentSnapshot].
 */
class FirestoreDocumentSnapshot(
    private val actual: ActualDocumentSnapshot,
) : DocumentSnapshot {

  override fun <T : Any> toObject(valueClass: KClass<T>): T? = actual.toObject(valueClass.java)
}
