package ch.epfl.sdp.mobile.application.authentication

/** An enumeration which represents the result of an authentication operation. */
enum class AuthenticationResult {

  /** Indicates that there was a success during authentication. */
  Success,

  /** Indicates that there was a failure during authentication. */
  Failure,

  /** Indicates that there was a failure during authentication caused by a weak password. */
  FailureBadPassword,

  /**
   * Indicates that there was a failure during authentication caused by an incorrectly typed email.
   */
  FailureIncorrectEmailFormat,

  // TODO : Some specific failures could be added here, like FailureBadPassword, etc.
}
