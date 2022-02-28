package ch.epfl.sdp.mobile.data.api

import kotlinx.coroutines.flow.Flow

/**
 * An interface which represents all the endpoints and available features for authenticating users
 * of the Pawnies application.
 */
interface AuthenticationApi {

  /** A class which represents a type-safe [Email] and its [text]. */
  @JvmInline value class Email(val text: String)

  /** A class which represents a type-safe password and its [text]. */
  @JvmInline value class Password(val text: String)

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

      /** The [Email] of the currently logged in user. */
      val email: Email

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
   * @param email the [Email] to use for authentication.
   * @param password the [Password] to use for authentication.
   */
  suspend fun signInWithEmail(
      email: Email,
      password: Password,
  ): AuthenticationResult

  /**
   * Attempts to sign up and create an account with the provided email and password.
   *
   * @param email the [Email] to use for authentication.
   * @param password the [Password] to use for authentication.
   */
  suspend fun signUpWithEmail(
      email: Email,
      password: Password,
  ): AuthenticationResult
}
