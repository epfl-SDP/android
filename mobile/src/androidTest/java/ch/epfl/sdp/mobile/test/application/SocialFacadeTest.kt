package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SocialFacadeTest {

  @Test
  fun search_returnWithEmptySearchString() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = SocialFacade(auth, store)

    Truth.assertThat(facade.search("").first()).isEmpty()
  }

  @Test
  fun search_successfullySearch() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("uid", ProfileDocument(name = "test")) }
    }
    val facade = SocialFacade(auth, store)

    val user = facade.search("test").first()[0]
    Truth.assertThat(user.name).isEqualTo("test")
  }

  @Test
  fun get_successfully() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("uid", ProfileDocument(name = "test")) }
    }
    val facade = SocialFacade(auth, store)

    val user = facade.get("test").first()
    Truth.assertThat(user.name).isEqualTo("test")
  }

  @Test
  fun get_unsuccessfully() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = SocialFacade(auth, store)

    val user = facade.get("test").firstOrNull()

    Truth.assertThat(user).isEqualTo(null)
  }
}
