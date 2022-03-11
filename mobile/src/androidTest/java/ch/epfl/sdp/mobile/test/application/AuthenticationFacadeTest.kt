package ch.epfl.sdp.mobile.test.application

import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.authentication.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.application.authentication.AuthenticationResult.Success
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AuthenticationFacadeTest {

  @Test
  fun signIn_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = buildAuth { user("alexandre.piveteau@epfl.ch", "password") }
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)

    assertThat(facade.signInWithEmail("alexandre.piveteau@epfl.ch", "password")).isEqualTo(Success)
  }

  @Test
  fun signIn_withEmptyAuthResult_returnsFailure() = runTest {
    val auth = emptyAuth()
    val firestore = emptyStore()

    val facade = AuthenticationFacade(auth, firestore)

    assertThat(facade.signInWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun signUp_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val api = AuthenticationFacade(auth, store)

    assertThat(api.signUpWithEmail("email", "name", "password")).isEqualTo(Success)
  }

  @Test
  fun signUp_withoutAuthResult_succeeds() = runTest {
    val auth = mockk<Auth>()
    val firestore = emptyStore()

    coEvery { auth.signUpWithEmail(any(), any()) } returns Auth.AuthenticationResult.Success(null)

    val facade = AuthenticationFacade(auth, firestore)

    assertThat(facade.signUpWithEmail("email", "name", "password")).isEqualTo(Success)
  }

  @Test
  fun signUp_failingAuth_returnsFailure() = runTest {
    val auth = mockk<Auth>()
    val firestore = emptyStore()

    coEvery { auth.signUpWithEmail(any(), any()) } returns Auth.AuthenticationResult.FailureInternal

    val facade = AuthenticationFacade(auth, firestore)

    assertThat(facade.signUpWithEmail("email", "name", "password")).isEqualTo(Failure)
  }

  @Test
  fun firstNonLoadingUser_canSignOut() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val facade = AuthenticationFacade(auth, store)
    facade.signUpWithEmail("email", "name", "password")
    facade.currentUser.filterIsInstance<AuthenticatedUser>().first().signOut()

    assertThat(auth.currentUser.first()).isNull()
    assertThat(facade.currentUser.filterNotNull().first()).isEqualTo(NotAuthenticatedUser)
  }

  @Test
  fun authenticatedUserCanUpdateItsProfileAndCanSeeUpdatedValues() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)

    facade.signUpWithEmail("email@email.com", "name", "password")

    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    userAuthenticated.update {
      emoji("🇺🇦")
      backgroundColor(ProfileColor.Pink)
      name("MyNewName")
    }

    val updatedUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    assertThat(updatedUser.name).isEqualTo("MyNewName")
    assertThat(updatedUser.emoji).isEqualTo("🇺🇦")
    assertThat(updatedUser.backgroundColor).isEqualTo(ProfileColor.Pink)
  }

  @Test
  fun gettingProfileWithNullValuesAssignsDefaultValues() = runTest {
    val auth = buildAuth { user("email@example.org", "password") }
    val store = buildStore { collection("users") { document("uid", ProfileDocument()) } }

    val facade = AuthenticationFacade(auth, store)
    facade.signInWithEmail("email@example.org", "password")

    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val following = userAuthenticated.following.first()

    val profile = following[0]
    assertThat(profile.name).isEqualTo("")
    assertThat(profile.emoji).isEqualTo("😎")
    assertThat(profile.backgroundColor).isEqualTo(ProfileColor.Pink)
  }
}
