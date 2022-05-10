package ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreFieldValue.mapFirestoreFieldValue
import com.google.firebase.firestore.DocumentReference as ActualDocumentReference
import com.google.firebase.firestore.SetOptions
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * An implementation of [DocumentReference] which uses a Firestore document reference
 * under-the-hood.
 *
 * @param actual the [ActualDocumentReference].
 */
class FirestoreDocumentReference(
    val actual: ActualDocumentReference,
) : DocumentReference {

  override val id: String
    get() = actual.id

  override fun collection(path: String): CollectionReference =
      FirestoreCollectionReference(actual.collection(path))

  override fun asDocumentSnapshotFlow(): Flow<DocumentSnapshot?> =
      callbackFlow {
            val registration =
                actual.addSnapshotListener { value, error ->
                  value?.let { trySend(FirestoreDocumentSnapshot(it)) }
                  error?.let { close(it) }
                }
            awaitClose { registration.remove() }
          }
          .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  override suspend fun delete() {
    actual.delete().await()
  }

  override suspend fun update(scope: DocumentEditScope.() -> Unit) {
    val values = RecordingDocumentEditScope().apply(scope).mutations
    actual.set(values.toFirestoreDocument(), SetOptions.merge()).await()
  }

  override suspend fun set(scope: DocumentEditScope.() -> Unit) {
    val values = RecordingDocumentEditScope().apply(scope).mutations
    actual.set(values.toFirestoreDocument()).await()
  }

  override suspend fun <T : Any> set(value: T, valueClass: KClass<T>) {
    actual.set(value).await()
  }
}

/**
 * Maps a flat map of [FieldPath] to values into a [Map] that's Firestore-friendly. More
 * specifically, the resulting map will have nested maps to represent composed field paths.
 *
 * @receiver the [Map] of [FieldPath] to values, without any nesting.
 * @return the [Map] that will be given to Firestore.
 */
@Suppress("Unchecked_Cast")
fun Map<FieldPath, Any?>.toFirestoreDocument(): Map<String, Any?> {
  val document = mutableMapOf<String, Any?>()
  for ((path, value) in this) {
    var root = document
    val segments = path.segments.toMutableList()
    while (segments.size > 1) {
      val segment = segments[0]
      val map = root[segment] as? MutableMap<String, Any?> ?: mutableMapOf()
      root[segment] = map
      root = map
      segments.removeFirst()
    }
    root[segments[0]] = value.mapFirestoreFieldValue()
  }
  return document
}
