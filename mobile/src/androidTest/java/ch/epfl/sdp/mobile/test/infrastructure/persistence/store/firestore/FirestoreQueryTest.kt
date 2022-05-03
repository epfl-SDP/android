package ch.epfl.sdp.mobile.test.infrastructure.persistence.store.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query.Direction.Ascending
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Query.Direction.Descending
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.firestore.FirestoreQuery
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
  fun orderBy_delegatesToOrderByAscending() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.orderBy(FieldPath.of("field"), Query.Direction.ASCENDING) } returns result
    reference.orderBy("field")
    verify { query.orderBy(FieldPath.of("field"), Query.Direction.ASCENDING) }
  }

  @Test
  fun orderByAscending_delegatesToOrderByAscending() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.orderBy(FieldPath.of("field"), Query.Direction.ASCENDING) } returns result
    reference.orderBy("field", Ascending)
    verify { query.orderBy(FieldPath.of("field"), Query.Direction.ASCENDING) }
  }

  @Test
  fun orderByDescending_delegatesToOrderByAscending() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.orderBy(FieldPath.of("field"), Query.Direction.DESCENDING) } returns result
    reference.orderBy("field", Descending)
    verify { query.orderBy(FieldPath.of("field"), Query.Direction.DESCENDING) }
  }

  @Test
  fun whereGreaterThanNotInclusive_delegatesWhereGreaterThan() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereGreaterThan(FieldPath.of("field"), "value") } returns result
    reference.whereGreaterThan("field", "value", inclusive = false)
    verify { query.whereGreaterThan(FieldPath.of("field"), "value") }
  }

  @Test
  fun whereGreaterThanInclusive_delegatesWhereGreaterThanOrEqual() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereGreaterThanOrEqualTo(FieldPath.of("field"), "value") } returns result
    reference.whereGreaterThan("field", "value", inclusive = true)
    verify { query.whereGreaterThanOrEqualTo(FieldPath.of("field"), "value") }
  }

  @Test
  fun whereLessThanNotInclusive_delegatesWhereLessThan() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereLessThan(FieldPath.of("field"), "value") } returns result
    reference.whereLessThan("field", "value", inclusive = false)
    verify { query.whereLessThan(FieldPath.of("field"), "value") }
  }

  @Test
  fun whereLessThanInclusive_delegatesWhereLessThanOrEqual() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereLessThanOrEqualTo(FieldPath.of("field"), "value") } returns result
    reference.whereLessThan("field", "value", inclusive = true)
    verify { query.whereLessThanOrEqualTo(FieldPath.of("field"), "value") }
  }

  @Test
  fun whereEquals_delegatesWhereEqualTo() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereEqualTo(FieldPath.of("field"), null) } returns result
    reference.whereEquals("field", null)
    verify { query.whereEqualTo(FieldPath.of("field"), null) }
  }

  @Test
  fun whereNotEquals_delegatesWhereNotEqualTo() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereNotEqualTo(FieldPath.of("field"), null) } returns result
    reference.whereNotEquals("field", null)
    verify { query.whereNotEqualTo(FieldPath.of("field"), null) }
  }

  @Test
  fun whereArrayContains_delegatesWhereArrayContains() = runTest {
    val query = mockk<Query>()
    val result = mockk<Query>()
    val reference = FirestoreQuery(query)

    every { query.whereArrayContains(FieldPath.of("field"), "value") } returns result
    reference.whereArrayContains("field", "value")
    verify { query.whereArrayContains(FieldPath.of("field"), "value") }
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
