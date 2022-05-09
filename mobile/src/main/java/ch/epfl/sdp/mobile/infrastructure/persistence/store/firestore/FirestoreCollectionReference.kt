package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import com.google.firebase.firestore.CollectionReference as ActualCollectionReference

/**
 * An implementation of [CollectionReference] which uses a Firestore collection reference
 * under-the-hood.
 *
 * @param actual the [ActualCollectionReference].
 */
class FirestoreCollectionReference(
    val actual: ActualCollectionReference,
) : CollectionReference<FirestoreDocumentReference>, Query by FirestoreQuery(actual) {

  override fun document(): FirestoreDocumentReference =
      FirestoreDocumentReference(actual.document())

  override fun document(path: String): FirestoreDocumentReference =
      FirestoreDocumentReference(actual.document(path))
}
