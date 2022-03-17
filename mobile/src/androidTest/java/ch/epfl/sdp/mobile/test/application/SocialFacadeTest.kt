package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.ui.social.Person
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SocialFacadeTest {

  private class FakePerson : Person {
    override val backgroundColor: Profile.Color = Profile.Color.Default
    override val name: String = "andy"
    override val emoji: String = ":3"
    override val uid: String = "1"
  }

  @Test
  fun search_returnWithEmptySearchString() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("uid", ProfileDocument(name = "test")) }
    }
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
  fun follow_addUidOfFollowedProfile() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "b") }
    val store = buildStore {
      collection("users") { document("0", ProfileDocument(uid = "0"));
        document("1", ProfileDocument(uid = "1")) }
    }
    val facade = SocialFacade(auth, store)

    facade.follow(FakePerson())
  }
}
