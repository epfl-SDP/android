package ch.epfl.sdp.mobile.application.authentication

/** An enumeration which represents the result of an authentication operation. */
enum class AuthenticationResult {

  /** Indicates that there was a success during authentication. */
  Success,

  /** Indicates that there was a failure during authentication. */
  Failure,

  /** Indicates that the password the user chose for their account is not strong enough. */
  FailureBadPassword,

  /** Indicates that the email the user typed is malformed. */
  FailureIncorrectEmailFormat,

  /** Indicates that an account already exists with the given email. */
  FailureExistingAccount,

  /** Indicates that the given password associated to an email is incorrect. */
  FailureIncorrectPassword,

  /**
   * Indicates that the user account corresponding to an email does not exist or has been disabled.
   */
  FailureInvalidUser,

}
