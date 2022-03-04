package ch.epfl.sdp.mobile.data.api.firebase

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Success
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * An implementation of the [AuthenticationApi] which uses [FirebaseAuth] internally.
 *
 * @param auth the [FirebaseAuth] instance which will be used to handle authentication.
 */
class FirebaseAuthenticationApi(private val auth: FirebaseAuth) : AuthenticationApi {

  override val currentUser: Flow<AuthenticationApi.User> =
      auth.currentUserFlow()
          .map { user ->
            user?.let { FirebaseUserAdapter(it, auth) } ?: AuthenticationApi.User.NotAuthenticated
          }
          .onStart { emit(AuthenticationApi.User.Loading) }

  /**
   * Runs the [block] and maps the resulting [AuthResult] to an [AuthenticationResult].
   *
   * @param block the block of code which is used to interact with Firebase.
   */
  private suspend fun authenticate(
      block: suspend () -> AuthResult?,
  ): AuthenticationResult =
      try {
        val result = block()
        if (result?.user != null) Success else Failure
      } catch (other: Throwable) {
        Failure
      }

  override suspend fun signInWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult = authenticate {
    auth.signInWithEmailAndPassword(email, password).await()
  }

  override suspend fun signUpWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult = authenticate {
    auth.createUserWithEmailAndPassword(email, password).await()
  }
}

/**
 * Returns a [Flow] of the current [FirebaseUser].
 *
 * @receiver the [FirebaseAuth] from which the current user is retrieved.
 */
private fun FirebaseAuth.currentUserFlow(): Flow<FirebaseUser?> =
    callbackFlow {
          val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
          addAuthStateListener(listener)
          awaitClose { removeAuthStateListener(listener) }
        }
        .buffer(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

/**
 * An implementation of [AuthenticationApi.User.Authenticated] which wraps a [FirebaseUser].
 *
 * @param user the [FirebaseUser] which is wrapped.
 * @param auth the underlying [FirebaseAuth].
 */
private class FirebaseUserAdapter(
    user: FirebaseUser,
    private val auth: FirebaseAuth,
) : AuthenticationApi.User.Authenticated {
  override val email = user.email ?: ""
  override suspend fun signOut() {
    auth.signOut()
  }
}
