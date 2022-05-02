package ch.epfl.sdp.mobile.test.infrastructure.persistence.store

import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
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
}
