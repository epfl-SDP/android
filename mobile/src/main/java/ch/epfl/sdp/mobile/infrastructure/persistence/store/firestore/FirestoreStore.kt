package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import com.google.firebase.firestore.FirebaseFirestore

/**
 * An implementation of [Store] which uses Firestore under-the-hood.
 *
 * @param firestore the actual [FirebaseFirestore] instance.
 */
class FirestoreStore(
    private val firestore: FirebaseFirestore,
) : Store {

  override fun collection(path: String): CollectionReference =
      FirestoreCollectionReference(firestore.collection(path))
}
