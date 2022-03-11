package ch.epfl.sdp.mobile.application

import kotlinx.coroutines.flow.Flow

/**
 * An interface which represents all the endpoints and available features for authenticating users
 * of the Pawnies application.
 */
interface AuthenticationFacade {

  /**
   * An interface which represents the current authentication status of the user of the
   * [AuthenticationFacade].
   */
  sealed interface User {

    /** Indicates that no [User] is currently authenticated. */
    object NotAuthenticated : User

    /** Indicates that a [User] is currently authenticated. */
    interface Authenticated : User, Profile {

      interface UpdateScope {
        var emoji: String?
        var backgroundColor: ProfileColor?
        var name: String?
      }

      /** The email of the currently logged in user. */
      val email: String

      val following: Flow<List<Profile>>

      suspend fun update(block: UpdateScope.() -> Unit): Boolean

      /** Signs this user out of the [AuthenticationFacade]. */
      suspend fun signOut()
    }
  }

  interface Profile {
    val emoji: String
    val name: String
    val backgroundColor: ProfileColor
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
   * @param name the name to use for the user account.
   * @param password the password to use for authentication.
   */
  suspend fun signUpWithEmail(
      email: String,
      name: String,
      password: String,
  ): AuthenticationResult
}
