package ch.epfl.sdp.mobile.androidTest.application

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.emptyStore
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

    val user = facade.search("test").first()[0]
    Truth.assertThat(user.name).isEqualTo("test")
  }

  @Test
  fun search_matchesAllWithPrefix() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument(name = "alice"))
        document("2", ProfileDocument(name = "alexandre"))
        document("3", ProfileDocument(name = "bob"))
      }
    }
    val facade = SocialFacade(auth, store)

    val results = facade.search("al").first().map { it.name }
    Truth.assertThat(results).containsExactly("alexandre", "alice").inOrder()
  }

  @Test
  fun search_matchesNameWithOnlyMaxChars() = runTest {
    val name = CharArray(10) { Char.MAX_VALUE }.concatToString() // what a weird name
    val auth = emptyAuth()
    val store = buildStore { collection("users") { document("1", ProfileDocument(name = name)) } }
    val facade = SocialFacade(auth, store)

    val profile = facade.search(name).first()[0]
    Truth.assertThat(profile.name).isEqualTo(name)
  }

  @Test
  fun search_limitsResults() = runTest {
    val auth = emptyAuth()
    val store = buildStore {
      collection("users") {
        // Insert more profiles than MaxSearchResultCount.
        repeat(SocialFacade.MaxSearchResultCount.toInt() + 1) {
          document(it.toString(), ProfileDocument(name = "alexandre"))
        }
      }
    }
    val facade = SocialFacade(auth, store)

    val profiles = facade.search("alexandre").first()
    Truth.assertThat(profiles.size).isEqualTo(SocialFacade.MaxSearchResultCount)
  }

  @Test
  fun follow_addUidOfFollowedProfile() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "password1") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example@hotmail.com", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    user.follow(FakeProfile("other"))
    val fakePersonFollowing = user.following.first().map { it.uid }
    Truth.assertThat(fakePersonFollowing).contains("other")
  }

  @Test
  fun follow_removeUidOfFollowedProfile() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "password1") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example@hotmail.com", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    user.follow(FakeProfile("other"))
    user.unfollow((FakeProfile("other")))
    val fakePersonFollowing = user.following.first().map { it.uid }
    Truth.assertThat(fakePersonFollowing).doesNotContain("other")
  }

  @Test
  fun follow_unfollowProfileNotInFollowersDoesNothing() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "password1") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example@epfl.ch", "name", "password")
    val user = authenticationFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    user.unfollow((FakeProfile("other")))
    val fakePersonFollowing = user.following.first().map { it.uid }
    Truth.assertThat(fakePersonFollowing).isEmpty()
  }

  @Test
  fun following_newUserHasNoFollowings() = runTest {
    val auth = buildAuth { user("a@hotmail.com", "password1") }
    val store = buildStore { collection("users") { document("other", ProfileDocument()) } }
    val authenticationFacade = AuthenticationFacade(auth, store)

    authenticationFacade.signUpWithEmail("example@epfl.ch", "name", "password")
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

    val user = facade.profile("uid").firstOrNull()
    Truth.assertThat(user?.name).isEqualTo("test")
  }

  @Test
  fun get_unsuccessfully_userIsNotInDatabse() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = SocialFacade(auth, store)

    val user = facade.profile("test").firstOrNull()

    Truth.assertThat(user).isEqualTo(null)
  }
}
