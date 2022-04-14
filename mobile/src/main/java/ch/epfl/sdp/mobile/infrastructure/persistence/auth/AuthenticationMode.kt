package ch.epfl.sdp.mobile.infrastructure.persistence.auth

/** An enumeration representing the [AuthenticationMode] that a user chose. */
enum class AuthenticationMode {

  /** The mode in which a user wants to login to their existing account. */
  SignIn,

  /** The mode in which a user wants to create a new account. */
  SignUp,
}
