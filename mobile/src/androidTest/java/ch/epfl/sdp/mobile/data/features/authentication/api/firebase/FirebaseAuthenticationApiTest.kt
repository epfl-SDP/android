package ch.epfl.sdp.mobile.data.features.authentication.api.firebase

import ch.epfl.sdp.mobile.backend.store.fake.emptyStore
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Success
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User.Authenticated
import ch.epfl.sdp.mobile.data.api.firebase.FirebaseAuthenticationApi
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirebaseAuthenticationApiTest {

  @Test
  fun currentUser_startsWithLoading() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.currentUser.first()).isEqualTo(User.Loading)
  }

  @Test
  fun signIn_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = mockk<FirebaseAuth>()
    val result = mockk<AuthResult>()
    val user = mockk<FirebaseUser>()
    val firestore = emptyStore()

    every { result.user } returns user
    every { auth.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signInWithEmail("email", "password")).isEqualTo(Success)
  }

  @Test
  fun signIn_withEmptyAuthResult_returnsFailure() = runTest {
    val auth = mockk<FirebaseAuth>()
    val result = mockk<AuthResult>()
    val firestore = emptyStore()

    every { result.user } returns null
    every { auth.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signInWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun signUp_withSuccessfulAuthResult_returnsSuccess() = runTest {
    val auth = mockk<FirebaseAuth>()
    val result = mockk<AuthResult>()
    val user = mockk<FirebaseUser>()
    val firestore = emptyStore()

    every { result.user } returns user
    every { auth.createUserWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signUpWithEmail("email", "password")).isEqualTo(Success)
  }

  @Test
  fun signUp_withThrowingAuthResult_returnsFailure() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()

    every { auth.createUserWithEmailAndPassword(any(), any()) } returns
        Tasks.forException(Exception())

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signUpWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun signUp_withoutAuthResult_returnsFailure() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()

    every { auth.createUserWithEmailAndPassword(any(), any()) } returns Tasks.forResult(null)

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.signUpWithEmail("email", "password")).isEqualTo(Failure)
  }

  @Test
  fun firstNonLoadingUser_isEmittedByListener() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val user = mockk<FirebaseUser>()
    val email = "alexandre.piveteau@epfl.ch"

    every { user.email } returns email
    every { user.uid } returns "uid"
    every { auth.addAuthStateListener(any()) } answers
        { call ->
          val listener = call.invocation.args[0] as FirebaseAuth.AuthStateListener
          every { auth.currentUser } returns user
          listener.onAuthStateChanged(auth)
        }
    every { auth.removeAuthStateListener(any()) } returns Unit

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.currentUser.filterIsInstance<Authenticated>().first().email).isEqualTo(email)
    verify {
      auth.addAuthStateListener(any())
      auth.removeAuthStateListener(any())
    }
  }

  @Test
  fun firstNonLoadingUserWithoutEmail_isEmittedByListener() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val user = mockk<FirebaseUser>()

    every { user.email } returns null
    every { user.uid } returns "uid"
    every { auth.addAuthStateListener(any()) } answers
        { call ->
          val listener = call.invocation.args[0] as FirebaseAuth.AuthStateListener
          every { auth.currentUser } returns user
          listener.onAuthStateChanged(auth)
        }
    every { auth.removeAuthStateListener(any()) } returns Unit

    val api = FirebaseAuthenticationApi(auth, firestore)

    assertThat(api.currentUser.filterIsInstance<Authenticated>().first().email).isEqualTo("")
    verify {
      auth.addAuthStateListener(any())
      auth.removeAuthStateListener(any())
    }
  }

  @Test
  fun firstNonLoadingUser_canSignOut() = runTest {
    val auth = mockk<FirebaseAuth>()
    val firestore = emptyStore()
    val user = mockk<FirebaseUser>()

    every { user.email } returns null
    every { user.uid } returns "uid"
    every { auth.addAuthStateListener(any()) } answers
        { call ->
          val listener = call.invocation.args[0] as FirebaseAuth.AuthStateListener
          every { auth.currentUser } returns user
          listener.onAuthStateChanged(auth)
        }
    every { auth.removeAuthStateListener(any()) } returns Unit
    every { auth.signOut() } returns Unit

    val api = FirebaseAuthenticationApi(auth, firestore)

    api.currentUser.filterIsInstance<Authenticated>().first().signOut()
    verify { auth.signOut() }
  }
}
