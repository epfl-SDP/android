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
 * @property actual the [ActualFirebaseAuth].
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

  /** An enumeration representing the [AuthenticationMode] that a user chose. */
  private enum class AuthenticationMode {

    /** The mode in which a user wants to login to their existing account. */
    SignIn,

    /** The mode in which a user wants to create a new account. */
    SignUp,
  }

  /**
   * Runs the [block] and maps the resulting [AuthResult] to an [AuthenticationResult].
   *
   * @param mode the [AuthenticationMode] used to authenticate.
   * @param block the block of code which is used to interact with Firebase.
   */
  private suspend fun authenticate(
      mode: AuthenticationMode,
      block: suspend () -> AuthResult?,
  ): AuthenticationResult =
      try {
        val result = block()
        val user = result?.user?.let(::FirebaseUser)
        AuthenticationResult.Success(user)
      } catch (other: Throwable) {
        when (other) {
          is FirebaseAuthWeakPasswordException -> AuthenticationResult.Failure.BadPassword
          is FirebaseAuthInvalidCredentialsException ->
              when (mode) {
                AuthenticationMode.SignIn -> AuthenticationResult.Failure.IncorrectPassword
                AuthenticationMode.SignUp -> AuthenticationResult.Failure.IncorrectEmailFormat
              }
          is FirebaseAuthUserCollisionException -> AuthenticationResult.Failure.ExistingAccount
          is FirebaseAuthInvalidUserException -> AuthenticationResult.Failure.InvalidUser
          else -> AuthenticationResult.Failure.Internal
        }
      }

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult =
      authenticate(AuthenticationMode.SignIn) {
        actual.signInWithEmailAndPassword(email, password).await()
      }

  override suspend fun signUpWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult =
      authenticate(AuthenticationMode.SignUp) {
        actual.createUserWithEmailAndPassword(email, password).await()
      }

  override suspend fun signOut() {
    actual.signOut()
  }
}
