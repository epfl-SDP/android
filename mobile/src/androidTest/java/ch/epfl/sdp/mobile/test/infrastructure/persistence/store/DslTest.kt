package ch.epfl.sdp.mobile.test.infrastructure.persistence.store

import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Test

class DslTest {

  data class User(
      val name: String? = null,
      val friends: List<String>? = null,
  )

  private val alexandre = User("Alexandre")
  private val chau = User("Chau", listOf("Matthieu"))
  private val matthieu = User("Matthieu", listOf("Chau"))

  @Test
  fun missingDocument_isEmpty() = runTest {
    val store = emptyStore()
    val value = store.collection("collection").document("document").asFlow<User>().first()
    assertThat(value).isNull()
  }

  @Test
  fun missingDocument_isEmptyList() = runTest {
    val store = emptyStore()
    val value = store.collection("col").asFlow<User>().first()
    assertThat(value).isEmpty()
  }

  @Test
  fun collectingFlow_seesNewDocuments() = runTest {
    val store = emptyStore()
    launch {
      val users = store.collection("col").asFlow<User>().filter { it.isNotEmpty() }.first()
      assertThat(users).containsExactly(alexandre)
    }
    yield() // Ensure that the flow starts collecting before we add the document.
    store.collection("col").document("a").set(alexandre)
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

  @Test
  fun whereEquals_works() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", alexandre)
        document("chau", chau)
        document("matthieu", matthieu)
      }
    }
    val users = store.collection("users").whereEquals("name", "Alexandre").asFlow<User>().first()
    assertThat(users.single()).isEqualTo(alexandre)
  }

  @Test
  fun whereNotEquals_works() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", alexandre)
        document("chau", chau)
        document("matthieu", matthieu)
      }
    }
    val users = store.collection("users").whereNotEquals("name", "Alexandre").asFlow<User>().first()
    assertThat(users.size).isEqualTo(2)
    assertThat(users.toSet()).containsExactly(chau, matthieu)
  }

  @Test
  fun whereArrayContains_works() = runTest {
    val store = buildStore {
      collection("users") {
        document("alexandre", alexandre)
        document("chau", chau)
        document("matthieu", matthieu)
      }
    }
    val users =
        store.collection("users").whereArrayContains("friends", "Matthieu").asFlow<User>().first()
    assertThat(users.single()).isEqualTo(chau)
  }
}
