package ch.epfl.sdp.mobile.infrastructure.persistence.auth.firebase

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth.AuthenticationResult
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth as ActualFirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * An implementation of the [Auth] API which encapsulates an [ActualFirebaseAuth] and adapts it.
 *
 * @param actual the [ActualFirebaseAuth].
 */
class FirebaseAuth(private val actual: ActualFirebaseAuth) : Auth {

  override val currentUser: Flow<User?>
    get() =
        callbackFlow {
          val listener = AuthStateListener { auth -> trySend(auth.currentUser) }
          actual.addAuthStateListener(listener)
          awaitClose { actual.removeAuthStateListener(listener) }
        }
            .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
            .map { it?.let(::FirebaseUser) }

  /**
   * Runs the [block] and maps the resulting [AuthResult] to an [AuthenticationResult].
   *
   * @param block the block of code which is used to interact with Firebase.
   */
  private suspend fun authenticate(
      block: suspend () -> AuthResult?,
      type: String,
  ): AuthenticationResult =
      try {
        val result = block()
        val user = result?.user?.let(::FirebaseUser)
        AuthenticationResult.Success(user)
      } catch (other: Throwable) {
        when (other) {
          is FirebaseAuthWeakPasswordException -> AuthenticationResult.FailureBadPassword
          is FirebaseAuthInvalidCredentialsException ->
              when (type) {
                "SignIn" -> AuthenticationResult.FailureIncorrectPassword
                "SignUp" -> AuthenticationResult.FailureIncorrectEmailFormat
                else -> AuthenticationResult.FailureInternal
              }
          is FirebaseAuthUserCollisionException -> AuthenticationResult.FailureExistingAccount
          is FirebaseAuthInvalidUserException -> AuthenticationResult.FailureInvalidUser
          else -> AuthenticationResult.FailureInternal
        }
      }

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult =
      authenticate({ actual.signInWithEmailAndPassword(email, password).await() }, "SignIn")

  override suspend fun signUpWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult =
      authenticate({ actual.createUserWithEmailAndPassword(email, password).await() }, "SignUp")

  override suspend fun signOut() {
    actual.signOut()
  }
}
