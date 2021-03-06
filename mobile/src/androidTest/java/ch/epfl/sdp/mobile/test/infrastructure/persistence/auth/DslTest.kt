package ch.epfl.sdp.mobile.test.infrastructure.persistence.auth

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth.AuthenticationResult.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DslTest {

  @Test
  fun emptyAuth_hasCurrentUser() = runTest {
    val auth = emptyAuth()
    val user = auth.currentUser.first()

    assertThat(user).isNull()
  }

  @Test
  fun emptyAuth_cantSignIn() = runTest {
    val auth = emptyAuth()
    val result = auth.signInWithEmail("email@epfl.ch", "password")

    assertThat(result).isEqualTo(Failure.InvalidUser)
  }

  @Test
  fun given_buildAuthWithOneUser_when_getsCurrentUser_then_isEmpty() = runTest {
    val auth = buildAuth { user("alexandre.piveteau@epfl.ch", "password") }
    val result = auth.currentUser.first()
    assertThat(result).isNull()
  }

  @Test
  fun existingUser_canSignIn() = runTest {
    val auth = buildAuth { user("alexandre.piveteau@epfl.ch", "password") }
    val result = auth.signInWithEmail("alexandre.piveteau@epfl.ch", "password")

    assertThat(result).isInstanceOf(Success::class.java)
  }

  @Test
  fun existingUser_cantSignUp() = runTest {
    val auth = buildAuth { user("alexandre.piveteau@epfl.ch", "password") }
    val result = auth.signUpWithEmail("alexandre.piveteau@epfl.ch", "password")

    assertThat(result).isEqualTo(Failure.ExistingAccount)
  }

  @Test
  fun signUp_makesUserCurrent() = runTest {
    val auth = emptyAuth()
    val result = auth.signUpWithEmail("alexandre.piveteau@epfl.ch", "password") as Success
    val current = auth.currentUser.first()

    assertThat(result.user).isEqualTo(current)
  }

  @Test
  fun canSignIn_afterSignUp() = runTest {
    val auth = buildAuth { user("alexandre.piveteau@epfl.ch", "password") }
    auth.signUpWithEmail("john.cena@epfl.ch", "password")
    val result = auth.signInWithEmail("alexandre.piveteau@epfl.ch", "password") as Success

    assertThat(auth.currentUser.first()).isEqualTo(result.user)
  }

  @Test
  fun cantSignIn_withBadPassword() = runTest {
    val auth = buildAuth { user("alexandre.piveteau@epfl.ch", "password") }
    val result = auth.signInWithEmail("alexandre.piveteau@epfl.ch", "bad")

    assertThat(result).isEqualTo(Failure.IncorrectPassword)
  }

  @Test
  fun duplicateUid_keepsFirstAccount() = runTest {
    val auth = buildAuth {
      user("alexandre.piveteau@epfl.ch", "password", "uid")
      user("lars.barmettler@epfl.ch", "password", "uid")
    }
    val result = auth.signInWithEmail("alexandre.piveteau@epfl.ch", "password") as Success

    assertThat(auth.currentUser.first()).isEqualTo(result.user)
  }
}
