package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store

import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslOrderingTest {

  data class AnyDocument(val value: Any?, @DocumentId val name: String? = null)
  data class BooleanDocument(val value: Boolean?, @DocumentId val name: String? = null)
  data class NumberDocument(val value: Number?, @DocumentId val name: String? = null)
  data class StringDocument(val value: String?, @DocumentId val name: String? = null)

  @Test
  fun oneNullValueHasNoOrder() = runTest {
    val store = buildStore { collection("docs") { document("a", AnyDocument(null)) } }
    val list = store.collection("docs").orderBy("value").asFlow<AnyDocument>().first()
    assertThat(list).containsExactly(AnyDocument(null, "a"))
  }

  @Test
  fun insertingNumbersUpTo100_areReadInOrder() = runTest {
    val store = emptyStore()
    val numbers = List(100) { it }
    numbers.shuffled().forEach { number ->
      store.collection("numbers").document().set(NumberDocument(number))
    }
    val list =
        store.collection("numbers").orderBy("value").asFlow<NumberDocument>().first().map { doc ->
          requireNotNull(doc?.value)
        }
    assertThat(list).isEqualTo(numbers)
  }

  @Test
  fun numericalValues_areReadInOrder() = runTest {
    val store = emptyStore()
    val floats = List(100) { it * 2f } // 0, ..., 200
    val ints = List(100) { it * 2 + 1 } // 1, ..., 201
    val sortedAsDouble = (floats + ints).map { it.toDouble() }.sorted()
    val shuffled = (floats + ints).shuffled()

    shuffled.forEach { store.collection("docs").document().set(NumberDocument(it)) }
    val list = store.collection("docs").orderBy("value").asFlow<NumberDocument>().first()

    assertThat(list.mapNotNull { it?.value?.toDouble() }).isEqualTo(sortedAsDouble)
  }

  @Test
  fun mixedTypes_areReadInOrder() = runTest {
    val store = emptyStore()
    val nullList = List(50) { AnyDocument(null) }
    val falseList = List(25) { AnyDocument(false) }
    val trueList = List(25) { AnyDocument(true) }
    val intList = List(25) { AnyDocument(it) }
    val stringList = List(25) { AnyDocument(('a' + it).toString()) }
    val sorted = nullList + falseList + trueList + intList + stringList
    val mixed = sorted.shuffled()

    mixed.forEach { store.collection("docs").document().set(it) }
    val list = store.collection("docs").orderBy("value").asFlow<AnyDocument>().first()

    assertThat(list.map { it?.value }).isEqualTo(sorted.map { it.value })
  }

  @Test
  fun whereGreaterThan_filersNullValues() = runTest {
    val store = buildStore { collection("docs") { document("a", AnyDocument(null)) } }
    val flow = store.collection("docs").whereGreaterThan("value", false).asFlow<AnyDocument>()
    assertThat(flow.first().size).isEqualTo(0)
  }

  @Test
  fun whereLessThan_filtersNullValues() = runTest {
    val store = buildStore { collection("docs") { document("a", AnyDocument(null)) } }
    val flow = store.collection("docs").whereLessThan("value", false).asFlow<AnyDocument>()
    assertThat(flow.first().size).isEqualTo(0)
  }

  @Test
  fun whereGreaterThanNotInclusive_doesNotIncludeEqualDocument() = runTest {
    val store = buildStore {
      collection("docs") {
        document("a", BooleanDocument(false))
        document("b", BooleanDocument(true))
      }
    }

    val query = store.collection("docs").whereGreaterThan("value", false, inclusive = false)
    val results = query.asFlow<BooleanDocument>().first().mapNotNull { it?.value }

    assertThat(results).containsExactly(true)
  }

  @Test
  fun whereGreaterThanInclusive_containsAllDocuments() = runTest {
    val store = buildStore {
      collection("docs") {
        document("a", BooleanDocument(false))
        document("b", BooleanDocument(true))
      }
    }

    val query = store.collection("docs").whereGreaterThan("value", false, inclusive = true)
    val results = query.asFlow<BooleanDocument>().first().mapNotNull { it?.value }

    assertThat(results).containsExactly(false, true)
  }

  @Test
  fun whereLessThanNotInclusive_doesNotIncludeEqualDocument() = runTest {
    val store = buildStore {
      collection("docs") {
        document("a", BooleanDocument(false))
        document("b", BooleanDocument(true))
      }
    }

    val query = store.collection("docs").whereLessThan("value", true, inclusive = false)
    val results = query.asFlow<BooleanDocument>().first().mapNotNull { it?.value }

    assertThat(results).containsExactly(false)
  }

  @Test
  fun whereLessThanInclusive_containsAllDocuments() = runTest {
    val store = buildStore {
      collection("docs") {
        document("a", BooleanDocument(false))
        document("b", BooleanDocument(true))
      }
    }

    val query = store.collection("docs").whereLessThan("value", true, inclusive = true)
    val results = query.asFlow<BooleanDocument>().first().mapNotNull { it?.value }

    assertThat(results).containsExactly(false, true)
  }

  @Test
  fun whereWithStrings_usesLexicographicOrdering() = runTest {
    val store = buildStore {
      collection("people") {
        document("a", StringDocument("alice"))
        document("b", StringDocument("bob"))
        document("c", StringDocument("bob bis"))
        document("d", StringDocument("dan"))
      }
    }

    val names =
        store
            .collection("people")
            .whereGreaterThan("value", "bob", inclusive = true)
            .whereLessThan("value", "dan", inclusive = false)
            .asFlow<StringDocument>()
            .first()
            .mapNotNull { it?.value }

    assertThat(names).containsExactly("bob", "bob bis")
  }
}
