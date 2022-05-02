package ch.epfl.sdp.mobile.test.infrastructure.persistence.store

import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslNestingTest {

  data class User(
      val id: String? = null,
      val profile: Profile? = null,
  )

  data class Profile(
      val name: String? = null,
      val age: Int? = null,
  )

  @Test
  fun given_emptyStore_when_creatingDocumentWithNestedFields_then_succeeds() = runTest {
    val reference = emptyStore().collection("hello").document()
    reference.set {
      this["id"] = "alex"
      this["user"] = mapOf("name" to "Alexandre", "age" to 24)
    }
    val user = reference.asFlow<User>().filterNotNull().first()

    assertThat(user).isEqualTo(User("alex", Profile("Alexandre", 24)))
  }

  @Test
  fun given_emptyStore_when_creatingDocumentWithoutValue_then_returnsEmptyNestedObject() = runTest {
    val reference = emptyStore().collection("hello").document()
    reference.set { this["id"] = "alex" }
    val user = reference.asFlow<User>().filterNotNull().first()

    assertThat(user).isEqualTo(User("alex", Profile(null, null)))
  }

  @Test
  fun given_emptyStore_when_creatingDocumentUsingObjectWithNestedFields_then_succeeds() = runTest {
    val reference = emptyStore().collection("hello").document()
    val document = User("alex", Profile("Alexandre", 24))
    reference.set(document)
    val fetched = reference.asFlow<User>().filterNotNull().first()

    assertThat(fetched).isEqualTo(document)
  }

  data class Level1(val text: String?, val node: Level2?)
  data class Level2(val text: String?, val node: Level3?)
  data class Level3(val text: String?)

  @Test
  fun given_emptyStore_when_creatingMultiLevelDocuments_then_properlySaved() = runTest {
    val reference = emptyStore().collection("hello").document()
    val document = Level1("one", Level2("two", Level3("three")))
    reference.set(document)
    val fetched = reference.asFlow<Level1>().filterNotNull().first()

    assertThat(fetched).isEqualTo(document)
  }
}
