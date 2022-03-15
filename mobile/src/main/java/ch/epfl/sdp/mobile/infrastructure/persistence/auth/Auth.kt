package ch.epfl.sdp.mobile.infrastructure.persistence.auth

import kotlinx.coroutines.flow.Flow

/**
 * An interface representing the primitives that the application may use to perform some basic
 * authentication duties.
 */
interface Auth {

  /** An enumeration which represents the result of an authentication operation. */
  sealed interface AuthenticationResult {

    /**
     * Indicates that there was a success during authentication.
     *
     * @param user the [User] that is currently set.
     */
    data class Success(val user: User?) : AuthenticationResult

    /** Indicates that there was a failure during authentication. */
    object FailureInternal : AuthenticationResult

    // TODO : Some specific failures could be added here, like FailureBadPassword, etc.
  }

  /**
   * Returns a [Flow] of the currently logged-in user. If no user is currently logged in, the [Flow]
   * will return some null values.
   */
  val currentUser: Flow<User?>

  /**
   * Attempts to sign in with the provided email and password.
   *
   * @param email the email to use for authentication.
   * @param password the password to use for authentication.
   */
  suspend fun signInWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult

  /**
   * Attempts to sign up and create an account with the provided email and password.
   *
   * @param email the email to use for authentication.
   * @param password the password to use for authentication.
   */
  suspend fun signUpWithEmail(
      email: String,
      password: String,
  ): AuthenticationResult

  /** Signs the current user out. If no user was currently connected, this will no-op. */
  suspend fun signOut()
}
