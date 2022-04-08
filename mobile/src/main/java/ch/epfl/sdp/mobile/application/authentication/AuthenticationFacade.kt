package ch.epfl.sdp.mobile.application.authentication

import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User as FacadeUser
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * An interface which represents all the endpoints and available features for authenticating users
 * of the Pawnies application.
 *
 * @param auth the [Auth] instance which will be used to handle authentication.
 * @param store the [Store] which is used to manage documents.
 */
class AuthenticationFacade(private val auth: Auth, private val store: Store) {

  /** A [Flow] of the current [AuthenticationUser]. */
  val currentUser: Flow<AuthenticationUser>
    get() =
        auth.currentUser.flatMapLatest { it.profileFlow(store) }.map {
          it?.toAuthenticatedUser(auth, store) ?: NotAuthenticatedUser
        }

  /**
   * Runs the [block] and maps the resulting [Auth.AuthenticationResult] to an
   * [AuthenticationResult].
   *
   * @param block the block of code which is used to interact with Firebase.
   */
  private suspend fun authenticate(
      block: suspend () -> Auth.AuthenticationResult,
  ): AuthenticationResult {
    return when (block()) {
      is Auth.AuthenticationResult.Success -> AuthenticationResult.Success
      Auth.AuthenticationResult.FailureInternal -> AuthenticationResult.Failure
      Auth.AuthenticationResult.FailureBadPassword -> AuthenticationResult.FailureBadPassword
      Auth.AuthenticationResult.FailureExistingAccount ->
          AuthenticationResult.FailureExistingAccount
      Auth.AuthenticationResult.FailureIncorrectEmailFormat ->
          AuthenticationResult.FailureIncorrectEmailFormat
      Auth.AuthenticationResult.FailureIncorrectPassword ->
          AuthenticationResult.FailureIncorrectPassword
      Auth.AuthenticationResult.FailureInvalidUser -> AuthenticationResult.FailureInvalidUser
    }
  }

  /**
   * Attempts to sign in with the provided email and password.
   *
   * @param email the email to use for authentication.
   * @param password the password to use for authentication.
   */
  suspend fun signInWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult = authenticate { auth.signInWithEmail(email, password) }

  /**
   * Attempts to sign up and create an account with the provided email and password.
   *
   * @param email the email to use for authentication.
   * @param name the name to use for the user account.
   * @param password the password to use for authentication.
   */
  suspend fun signUpWithEmail(
      email: String,
      name: String,
      password: String,
  ): AuthenticationResult = authenticate {
    val result = auth.signUpWithEmail(email, password)
    if (result is Auth.AuthenticationResult.Success && result.user != null) {
      store.collection("users").document(result.user.uid).set(ProfileDocument(name = name))
    }
    result
  }
}

private fun FacadeUser?.profileFlow(store: Store): Flow<Pair<FacadeUser, ProfileDocument?>?> {
  return if (this != null) {
    store.collection("users").document(uid).asFlow<ProfileDocument>().map { this to it }
  } else {
    flowOf(null)
  }
}

/**
 * Maps a [Pair] of [FacadeUser] and [ProfileDocument] to a [AuthenticationUser].
 *
 * @receiver the [Pair] which should be converted.
 * @param auth the [Auth] instance which is used to build the user.
 * @param store the [Store] instance which is used to build the user.
 */
private fun Pair<FacadeUser, ProfileDocument?>.toAuthenticatedUser(
    auth: Auth,
    store: Store,
): AuthenticatedUser =
    AuthenticatedUser(auth = auth, firestore = store, user = first, document = second)
