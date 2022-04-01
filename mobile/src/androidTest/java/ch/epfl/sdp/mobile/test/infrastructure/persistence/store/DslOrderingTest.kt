package ch.epfl.sdp.mobile.test.infrastructure.persistence.store

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
}
