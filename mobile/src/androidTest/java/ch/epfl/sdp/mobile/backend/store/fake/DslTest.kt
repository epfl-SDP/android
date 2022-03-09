package ch.epfl.sdp.mobile.backend.store.fake

import ch.epfl.sdp.mobile.backend.store.asFlow
import ch.epfl.sdp.mobile.backend.store.set
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslTest {

  data class User(val name: String? = null)

  private val alexandre = User("Alexandre")
  private val chau = User("Chau")
  private val matthieu = User("Matthieu")

  @Test
  fun missingDocument_isEmpty() = runTest {
    val store = emptyStore()
    val value = store.collection("collection").document("document").asFlow<User>().first()
    assertThat(value).isNull()
  }

  @Test
  fun nonNullDocument_isNotNull() = runTest {
    val store = buildStore { collection("users") { document("id", alexandre) } }
    val value = store.collection("users").document("id").asFlow<User>().first()
    assertThat(value).isEqualTo(alexandre)
  }

  @Test
  fun multipleDocuments_areAllPresent() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", alexandre)
        document("chau", chau)
        document("matthieu", matthieu)
      }
    }
    val people = store.collection("users").asFlow<User>().first().toSet()
    assertThat(people).containsExactly(alexandre, chau, matthieu)
  }

  @Test
  fun collection_canBeBuiltInSteps() = runTest {
    val store = buildStore {
      collection("users") { document("alexandre", alexandre) }
      collection("users") { document("chau", chau) }
      collection("users") { document("matthieu", matthieu) }
    }
    val people = store.collection("users").asFlow<User>().first().toSet()
    assertThat(people).containsExactly(alexandre, chau, matthieu)
  }

  data class SampleDocument(
      val title: String? = null,
      val subtitle: String? = null,
  )

  @Test
  fun document_isUpdated() = runTest {
    val store = buildStore { collection("users") { document("doc", SampleDocument()) } }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    val doc = store.collection("users").document("doc").asFlow<SampleDocument>().first()
    assertThat(doc).isEqualTo(SampleDocument(title = "Hello", subtitle = "World"))
  }

  @Test
  fun document_supportsSet() = runTest {
    val store = buildStore { collection("users") { document("doc", SampleDocument()) } }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    store.collection("users").document("doc").set { this["title"] = "Hello" }

    val data = store.collection("users").document("doc").asFlow<SampleDocument>().first()

    assertThat(data).isEqualTo(SampleDocument(title = "Hello", subtitle = null))
  }

  @Test
  fun document_supportsSetWithClass() = runTest {
    val store = emptyStore()
    store.collection("users").document("alexandre").set(alexandre)
    val data = store.collection("users").document("alexandre").asFlow<User>().first()
    assertThat(data).isEqualTo(alexandre)
  }

  @Test
  fun document_setWithKeyValues() = runTest {
    val store = emptyStore()
    store.collection("users").document("alexandre").set(mapOf("name" to "Alexandre"))
    val data = store.collection("users").document("alexandre").asFlow<User>().first()
    assertThat(data).isEqualTo(alexandre)
  }

  @Test
  fun document_supportsPartialUpdate() = runTest {
    val store = buildStore { collection("users") { document("doc", SampleDocument()) } }
    store.collection("users").document("doc").update {
      this["title"] = "Hello"
      this["subtitle"] = "World"
    }
    store.collection("users").document("doc").update { this["title"] = "Hello2" }

    val data = store.collection("users").document("doc").asFlow<SampleDocument>().first()

    assertThat(data).isEqualTo(SampleDocument(title = "Hello2", subtitle = "World"))
  }

  @Test
  fun limit_works() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", alexandre)
        document("chau", chau)
        document("matthieu", matthieu)
      }
    }
    val users = store.collection("users").limit(2).asFlow<User>().first()
    assertThat(users).hasSize(2)
  }
}
