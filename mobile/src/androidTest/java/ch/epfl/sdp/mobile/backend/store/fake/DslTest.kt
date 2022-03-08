package ch.epfl.sdp.mobile.backend.store.fake

import ch.epfl.sdp.mobile.backend.store.asFlow
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslTest {

  @Test
  fun missingDocument_isEmpty() = runTest {
    val store = emptyStore()
    val value = store.collection("collection").document("document").asFlow<Any>().first()
    assertThat(value).isNull()
  }

  @Test
  fun nullDocument_isNull() = runTest {
    val store = buildStore { collection("users") { document("alexandre", null) } }
    val value = store.collection("users").document("alexandre").asFlow<Any>().first()
    assertThat(value).isNull()
  }

  @Test
  fun nonNullDocument_isNotNull() = runTest {
    val store = buildStore { collection("users") { document("alexandre", "doc") } }
    val value = store.collection("users").document("alexandre").asFlow<String>().first()
    assertThat(value).isEqualTo("doc")
  }

  @Test
  fun multipleDocuments_areAllPresent() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", "alex")
        document("chau", "chau")
        document("matthieu", "matt")
      }
    }
    val people = store.collection("users").asFlow<String>().first().toSet()
    assertThat(people).containsExactly("alex", "chau", "matt")
  }

  @Test
  fun collection_canBeBuiltInSteps() = runTest {
    val store = buildStore {
      collection("users") { document("alexandre", "alex") }
      collection("users") { document("chau", "chau") }
      collection("users") { document("matthieu", "matt") }
    }
    val people = store.collection("users").asFlow<String>().first().toSet()
    assertThat(people).containsExactly("alex", "chau", "matt")
  }

  data class SampleDocument(
      val title: String? = null,
      val subtitle: String? = null,
  )

  @Test
  fun dataclassDocument_isUpdated() = runTest {
    val store = buildStore { collection("users") { dataclassDocument("doc", ::SampleDocument) } }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    val doc = store.collection("users").document("doc").asFlow<SampleDocument>().first()
    assertThat(doc).isEqualTo(SampleDocument(title = "Hello", subtitle = "World"))
  }

  @Test
  fun dataclassDocument_supportsSet() = runTest {
    val store = buildStore { collection("users") { dataclassDocument("doc", ::SampleDocument) } }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    store.collection("users").document("doc").set { this["title"] = "Hello" }

    val data = store.collection("users").document("doc").asFlow<SampleDocument>().first()

    assertThat(data).isEqualTo(SampleDocument(title = "Hello", subtitle = null))
  }

  @Test
  fun dataclassDocument_supportsUpdate() = runTest {
    val store = buildStore { collection("users") { dataclassDocument("doc", ::SampleDocument) } }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    store.collection("users").document("doc").update { this["title"] = "Hello2" }

    val data = store.collection("users").document("doc").asFlow<SampleDocument>().first()

    assertThat(data).isEqualTo(SampleDocument(title = "Hello2", subtitle = "World"))
  }

  data class User(
      val name: String? = null,
      val age: Int? = null,
  )

  @Test
  fun limit_works() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", "alex")
        document("matthieu", "matt")
        document("chau", "chau")
      }
    }
    val users = store.collection("users").limit(2).asFlow<String>().first()
    assertThat(users).hasSize(2)
  }
}
