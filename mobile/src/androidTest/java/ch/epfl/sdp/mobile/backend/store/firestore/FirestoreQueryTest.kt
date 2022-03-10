package ch.epfl.sdp.mobile.backend.store.firestore

import ch.epfl.sdp.mobile.backend.store.asFlow
import com.google.common.truth.Truth
import com.google.firebase.firestore.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirestoreQueryTest {

  @Test
  fun limit_delegatesLimit() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.limit(2) } returns result
    reference.limit(2)
    verify { query.limit(2) }
  }

  @Test
  fun asFlow_successfulListener() = runTest {
    val collection = mockk<Query>()
    val registration = mockk<ListenerRegistration>()
    val snapshot = mockk<QuerySnapshot>()
    val reference = FirestoreQuery(collection)

    every { snapshot.toObjects<String>(any()) } returns listOf("Success")
    every { collection.addSnapshotListener(any()) } answers
        { call ->
          @Suppress("UNCHECKED_CAST")
          val listener = call.invocation.args[0] as EventListener<QuerySnapshot>
          listener.onEvent(snapshot, null)
          registration
        }
    every { registration.remove() } returns Unit

    Truth.assertThat(reference.asFlow<List<String>>().first()).isEqualTo(listOf("Success"))
  }

  @Test
  fun asFlow_failingListener() = runTest {
    val collection = mockk<Query>()
    val registration = mockk<ListenerRegistration>()
    val reference = FirestoreQuery(collection)
    val exception =
        FirebaseFirestoreException("error", FirebaseFirestoreException.Code.PERMISSION_DENIED)

    every { collection.addSnapshotListener(any()) } answers
        { call ->
          val listener = call.invocation.args[0] as EventListener<*>
          listener.onEvent(null, exception)
          registration
        }
    every { registration.remove() } returns Unit

    val caught =
        try {
          reference.asFlow<Int>().first()
          error("Should not succeed.")
        } catch (ex: FirebaseFirestoreException) {
          ex
        }

    Truth.assertThat(caught).isEqualTo(exception)
  }
}
