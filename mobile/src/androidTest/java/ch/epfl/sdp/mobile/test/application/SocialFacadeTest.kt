package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SocialFacadeTest {

  private class FakeProfile(
      override val uid: String,
  ) : Profile {
    override val backgroundColor: Profile.Color = Profile.Color.Default
    override val name: String = "Andy"
    override val emoji: String = ":3"
    override val followed: Boolean = false
  }

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

    val user = facade.search("uid").first()[0]
    Truth.assertThat(user.name).isEqualTo("test")
  }

  @Test
  fun follow_addUidOfFollowedProfile() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "b") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    user.follow(FakeProfile("other"))
    val fakePersonFollowing = user.following.first().map { it.uid }
    Truth.assertThat(fakePersonFollowing).contains("other")
  }

  @Test
  fun follow_removeUidOfFollowedProfile() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "b") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    user.follow(FakeProfile("other"))
    user.unfollow((FakeProfile("other")))
    val fakePersonFollowing = user.following.first().map { it.uid }
    Truth.assertThat(fakePersonFollowing).doesNotContain("other")
  }

  @Test
  fun follow_unfollowProfileNotInFollowersDoesNothing() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "b") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    user.unfollow((FakeProfile("other")))
    val fakePersonFollowing = user.following.first().map { it.uid }
    Truth.assertThat(fakePersonFollowing).isEmpty()
  }

  @Test
  fun following_newUserHasNoFollowings() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "b") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val userFollowing = user.following.first()
    Truth.assertThat(userFollowing).isEmpty()
  }

  @Test
  fun get_successfully_userIsInDatabse() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") { document("uid", ProfileDocument(name = "test")) }
    }
    val facade = SocialFacade(auth, store)

    val user = facade.get("test").firstOrNull()
    Truth.assertThat(user?.name).isEqualTo("test")
  }

  @Test
  fun get_unsuccessfully_userIsNotInDatabse() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = SocialFacade(auth, store)

    val user = facade.get("test").firstOrNull()

    Truth.assertThat(user).isEqualTo(null)
  }
}
