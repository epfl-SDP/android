package ch.epfl.sdp.mobile.androidTest.application

import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.authentication.AuthenticationResult.*
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    assertThat(facade.signInWithEmail("email@epfl.ch", "password")).isEqualTo(FailureInvalidUser)
  }

  @Test
  fun given_nonEmptyAuth_when_theEnteredPasswordIsIncorrect_then_aFailureIncorrectPasswordOccurs() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password") }
      val firestore = emptyStore()

      val facade = AuthenticationFacade(auth, firestore)

      assertThat(facade.signInWithEmail("email@example.org", "incorrect"))
          .isEqualTo(FailureIncorrectPassword)
    }
  }

  @Test
  fun given_emptyAuth_when_theTypedPasswordIsWeak_then_aFailureBadPasswordOccurs() {
    runTest {
      val auth = emptyAuth()
      val firestore = emptyStore()

      val facade = AuthenticationFacade(auth, firestore)

      assertThat(facade.signUpWithEmail("email@epfl.ch", "Fred", "weak"))
          .isEqualTo(FailureBadPassword)
    }
  }

  @Test
  fun given_emptyAuth_when_theTypedEmailIsMalformed_then_aFailureIncorrectEmailFormatOccurs() {
    runTest {
      val auth = emptyAuth()
      val firestore = emptyStore()

      val facade = AuthenticationFacade(auth, firestore)

      assertThat(facade.signUpWithEmail("invalid-email", "Fred", "password"))
          .isEqualTo(FailureIncorrectEmailFormat)
    }
  }

  @Test
  fun given_nonEmptyAuth_when_theEmailAccountExists_then_aFailureExistingAccountOccurs() {
    runTest {
      val auth = buildAuth { user("email@example.org", "password") }
      val firestore = emptyStore()

      val facade = AuthenticationFacade(auth, firestore)

      assertThat(facade.signUpWithEmail("email@example.org", "Fred", "password"))
          .isEqualTo(FailureExistingAccount)
    }
  }

  @Test
  fun signUp_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val api = AuthenticationFacade(auth, store)

    assertThat(api.signUpWithEmail("email@epfl.ch", "name", "password")).isEqualTo(Success)
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

    coEvery { auth.signUpWithEmail(any(), any()) } returns
        Auth.AuthenticationResult.Failure.Internal

    val facade = AuthenticationFacade(auth, firestore)

    assertThat(facade.signUpWithEmail("email", "name", "password")).isEqualTo(Failure)
  }

  @Test
  fun firstNonLoadingUser_canSignOut() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()

    val facade = AuthenticationFacade(auth, store)
    facade.signUpWithEmail("email@epfl.ch", "name", "password")
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
      backgroundColor(Color.Orange)
      name("MyNewName")
    }

    val updatedUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    assertThat(updatedUser.name).isEqualTo("MyNewName")
    assertThat(updatedUser.emoji).isEqualTo("🇺🇦")
    assertThat(updatedUser.backgroundColor).isEqualTo(Color.Orange)
  }

  @Test
  fun authenticatedUserPartialUpdate_preservesExistingFields() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val facade = AuthenticationFacade(auth, store)

    facade.signUpWithEmail("email@email.com", "Alexandre", "password")

    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()

    userAuthenticated.update { emoji("🇺🇦") }

    val updatedUser = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    assertThat(updatedUser.name).isEqualTo("Alexandre")
  }

  @Test
  fun gettingProfileWithNullValuesAssignsDefaultValues() = runTest {
    val auth = buildAuth { user("email@example.org", "password") }
    val store = buildStore { collection("users") { document("uid", ProfileDocument()) } }

    val facade = AuthenticationFacade(auth, store)
    facade.signInWithEmail("email@example.org", "password")

    val userAuthenticated = facade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    val following =
        store
            .collection("users")
            .asFlow<ProfileDocument>()
            .map { it.mapNotNull { doc -> doc?.toProfile(userAuthenticated) } }
            .first()

    val profile = following[0]
    assertThat(profile.name).isEqualTo("")
    assertThat(profile.emoji).isEqualTo("😎")
    assertThat(profile.backgroundColor).isEqualTo(Color.Default)
  }
}
