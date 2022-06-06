package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.CollectionReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query
import com.google.firebase.firestore.CollectionReference as ActualCollectionReference

/**
 * An implementation of [CollectionReference] which uses a Firestore collection reference
 * under-the-hood.
 *
 * @property reference the [ActualCollectionReference].
 */
class FirestoreCollectionReference(
    private val reference: ActualCollectionReference,
) : CollectionReference, Query by FirestoreQuery(reference) {

  override fun document(): DocumentReference = FirestoreDocumentReference(reference.document())

  override fun document(path: String): DocumentReference =
      FirestoreDocumentReference(reference.document(path))
}
