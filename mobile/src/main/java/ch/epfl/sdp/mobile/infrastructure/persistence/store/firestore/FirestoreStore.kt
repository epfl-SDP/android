package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
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
) : Store {

  override fun collection(path: String): CollectionReference =
      FirestoreCollectionReference(firestore.collection(path))

  override suspend fun <R> transaction(
      block: Transaction<DocumentReference>.() -> R,
  ): R = firestore.runTransaction { block(FirestoreTransaction(it)) }.await()
}
