package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * An implementation of [Store] which uses Firestore under-the-hood.
 *
 * @param firestore the actual [FirebaseFirestore] instance.
 */
class FirestoreStore(
    private val firestore: FirebaseFirestore,
) : Store<FirestoreDocumentReference, FirestoreCollectionReference> {

  override fun collection(path: String): FirestoreCollectionReference =
      FirestoreCollectionReference(firestore.collection(path))

  override suspend fun <R> transaction(
      scope: Transaction<FirestoreDocumentReference>.() -> R,
  ): R = firestore.runTransaction { scope(FirestoreTransaction(it)) }.await()
}
