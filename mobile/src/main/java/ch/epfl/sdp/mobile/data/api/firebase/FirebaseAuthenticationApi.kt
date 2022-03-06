package ch.epfl.sdp.mobile.data.api.firebase

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Failure
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.AuthenticationResult.Success
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User.NotAuthenticated
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * An implementation of the [AuthenticationApi] which uses [FirebaseAuth] internally.
 *
 * @param auth the [FirebaseAuth] instance which will be used to handle authentication.
 */
class FirebaseAuthenticationApi(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthenticationApi {

  override val currentUser: Flow<User> =
      auth.currentUserFlow()
          .flatMapLatest {
            println("MATTHIEU1 : $it")
            it?.profileFlow(firestore) ?: flowOf(null)
          }
          .map { user ->
            println("MATTHIEU2 : User $user")
            user?.toAuthenticationUser(auth, firestore) ?: NotAuthenticated
          }
          .onStart { emit(User.Loading) }

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

private fun FirebaseUser.profileFlow(
    firestore: FirebaseFirestore,
): Flow<Pair<FirebaseUser, FirebaseProfileDocument?>> =
    firestore
        .collection("users")
        .document(uid)
        .asFlow()
        .map { it.toObject<FirebaseProfileDocument>() }
        .map { this to it }

/**
 * Maps a [FirebaseUser] to to an [AuthenticationApi.User].
 *
 * @receiver the [FirebaseUser] which should be converted.
 * @param auth the [FirebaseAuth] instance which is used to build the user.
 */
private fun Pair<FirebaseUser, FirebaseProfileDocument?>.toAuthenticationUser(
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
): User =
    FirebaseAuthenticatedUser(
        auth = auth,
        firestore = firestore,
        user = first,
        document = second,
    )

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
