package ch.epfl.sdp.mobile.androidTest.infrastructure.persistence.auth.firestore

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.firebase.FirebaseAuth
import com.google.android.gms.tasks.Tasks
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth as ActualFirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FirebaseAuthTest {

  @Test
  fun currentUser_returnsFirebaseUser() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val user = mockk<FirebaseUser>()
    val auth = FirebaseAuth(mock)

    every { user.uid } returns "uid"
    every { user.email } returns null
    every { mock.currentUser } returns user
    every { mock.addAuthStateListener(any()) }.answers {
      val listener = it.invocation.args[0] as AuthStateListener
      listener.onAuthStateChanged(mock)
    }
    every { mock.removeAuthStateListener(any()) } returns Unit

    val fetched = auth.currentUser.filterNotNull().first()
    assertThat(fetched.uid).isEqualTo("uid")
    assertThat(fetched.email).isNull()
  }

  @Test
  fun signUpWithEmail_returnsSuccessfully() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val result = mockk<AuthResult>()
    val auth = FirebaseAuth(mock)

    every { result.user } returns null
    every { mock.createUserWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val user = auth.signUpWithEmail("email", "password")

    assertThat(user).isInstanceOf(Auth.AuthenticationResult::class.java)
    verify { mock.createUserWithEmailAndPassword("email", "password") }
  }

  @Test
  fun signInWithEmail_returnsSuccessfullyWithNullUser() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val result = mockk<AuthResult>()
    val auth = FirebaseAuth(mock)

    every { result.user } returns null
    every { mock.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val user = auth.signInWithEmail("email", "password")

    assertThat(user).isInstanceOf(Auth.AuthenticationResult::class.java)
    verify { mock.signInWithEmailAndPassword("email", "password") }
  }

  @Test
  fun signInWithEmail_returnsSuccessfullyWithNullAuthResult() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val auth = FirebaseAuth(mock)

    every { mock.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(null)

    val user = auth.signInWithEmail("email", "password")

    assertThat(user).isInstanceOf(Auth.AuthenticationResult::class.java)
    verify { mock.signInWithEmailAndPassword("email", "password") }
  }

  @Test
  fun signInWithEmail_returnsSuccessfullyWithNonNullUser() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val user = mockk<FirebaseUser>()
    val result = mockk<AuthResult>()
    val auth = FirebaseAuth(mock)

    every { result.user } returns user
    every { mock.signInWithEmailAndPassword(any(), any()) } returns Tasks.forResult(result)

    val fetched = auth.signInWithEmail("email", "password")

    assertThat(fetched).isInstanceOf(Auth.AuthenticationResult::class.java)
    verify { mock.signInWithEmailAndPassword("email", "password") }
  }

  @Test
  fun signOut_actuallySignsOut() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val auth = FirebaseAuth(mock)
    every { mock.signOut() } returns Unit

    auth.signOut()

    verify { mock.signOut() }
  }

  @Test
  fun signIn_failsOnExceptions() = runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val auth = FirebaseAuth(mock)
    every { mock.signInWithEmailAndPassword(any(), any()) } throws Throwable()

    val result = auth.signInWithEmail("email", "password")

    assertThat(result).isEqualTo(Auth.AuthenticationResult.Failure.Internal)
  }

  @Test
  fun given_mockFirebaseAuthSignUp_when_weakPasswordExceptionOccurs_then_badPasswordFailureIsReturned() =
      runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val auth = FirebaseAuth(mock)
    every { mock.createUserWithEmailAndPassword(any(), any()) } throws
        FirebaseAuthWeakPasswordException("m", "m", null)

    val result = auth.signUpWithEmail("email", "password")

    assertThat(result).isEqualTo(Auth.AuthenticationResult.Failure.BadPassword)
  }

  @Test
  fun given_mockFirebaseAuthSignUp_when_invalidCredentialsExceptionOccurs_then_incorrectEmailFormatFailureIsReturned() =
      runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val auth = FirebaseAuth(mock)
    every { mock.createUserWithEmailAndPassword(any(), any()) } throws
        FirebaseAuthInvalidCredentialsException("m", "m")

    val result = auth.signUpWithEmail("email", "password")

    assertThat(result).isEqualTo(Auth.AuthenticationResult.Failure.IncorrectEmailFormat)
  }

  @Test
  fun given_mockFirebaseAuthSignIn_when_invalidCredentialsExceptionOccurs_then_incorrectPasswordFailureIsReturned() =
      runTest {
    val mock = mockk<ActualFirebaseAuth>()
    val auth = FirebaseAuth(mock)
    every { mock.signInWithEmailAndPassword(any(), any()) } throws
        FirebaseAuthInvalidCredentialsException("m", "m")

    val result = auth.signInWithEmail("email", "password")

    assertThat(result).isEqualTo(Auth.AuthenticationResult.Failure.IncorrectPassword)
  }
}
