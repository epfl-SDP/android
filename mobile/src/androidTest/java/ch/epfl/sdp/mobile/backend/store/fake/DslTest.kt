package ch.epfl.sdp.mobile.backend.store.fake

import ch.epfl.sdp.mobile.backend.store.asFlow
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslTest {

  @Test
  fun missingDocument_isEmpty() = runTest {
    val store = buildFakeStore {}
    val value = store.collection("collection").document("document").asFlow<Any>().first()
    assertThat(value).isNull()
  }

  @Test
  fun nullDocument_isNull() = runTest {
    val store = buildFakeStore { collection("users") { document("alexandre", null) } }
    val value = store.collection("users").document("alexandre").asFlow<Any>().first()
    assertThat(value).isNull()
  }

  @Test
  fun nonNullDocument_isNotNull() = runTest {
    val store = buildFakeStore { collection("users") { document("alexandre", "doc") } }
    val value = store.collection("users").document("alexandre").asFlow<String>().first()
    assertThat(value).isEqualTo("doc")
  }

  @Test
  fun multipleDocuments_areAllPresent() = runTest {
    val store = buildFakeStore {
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
    val store = buildFakeStore {
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
    val store = buildFakeStore {
      collection("users") { dataclassDocument("doc", ::SampleDocument) }
    }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    val doc = store.collection("users").document("doc").asFlow<SampleDocument>().first()
    assertThat(doc).isEqualTo(SampleDocument(title = "Hello", subtitle = "World"))
  }

  @Test
  fun dataclassDocument_supportsSet() = runTest {
    val store = buildFakeStore {
      collection("users") { dataclassDocument("doc", ::SampleDocument) }
    }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    store.collection("users").document("doc").set { this["title"] = "Hello" }

    val data = store.collection("users").document("doc").asFlow<SampleDocument>().first()

    assertThat(data).isNotNull()
    assertThat(requireNotNull(data).subtitle).isNull()
  }

  @Test
  fun dataclassDocument_supportsUpdate() = runTest {
    val store = buildFakeStore {
      collection("users") { dataclassDocument("doc", ::SampleDocument) }
    }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    store.collection("users").document("doc").update { this["title"] = "Hello2" }

    val data = store.collection("users").document("doc").asFlow<SampleDocument>().first()

    assertThat(data).isEqualTo(SampleDocument(title = "Hello2", subtitle = "World"))
  }
}
