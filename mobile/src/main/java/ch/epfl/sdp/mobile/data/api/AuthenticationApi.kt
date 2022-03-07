package ch.epfl.sdp.mobile.data.api

import kotlinx.coroutines.flow.Flow

/**
 * An interface which represents all the endpoints and available features for authenticating users
 * of the Pawnies application.
 */
interface AuthenticationApi {

  /**
   * An interface which represents the current authentication status of the user of the
   * [AuthenticationApi].
   */
  sealed interface User {

    /** Indicates that a [User] may or may not be present, but it's not clear yet. */
    object Loading : User

    /** Indicates that no [User] is currently authenticated. */
    object NotAuthenticated : User

    /** Indicates that a [User] is currently authenticated. */
    interface Authenticated : User {

      /** The email of the currently logged in user. */
      val email: String

      /** Signs this user out of the [AuthenticationApi]. */
      suspend fun signOut()
    }
  }

  /** A [Flow] of the current [User]. */
  val currentUser: Flow<User>

  /** An enumeration which represents the result of an authentication operation. */
  enum class AuthenticationResult {

    /** Indicates that there was a success during authentication. */
    Success,

    /** Indicates that there was a failure during authentication. */
    Failure,

    // TODO : Some specific failures could be added here, like FailureBadPassword, etc.
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
}
