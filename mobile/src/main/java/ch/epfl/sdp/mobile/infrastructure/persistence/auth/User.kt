package ch.epfl.sdp.mobile.infrastructure.persistence.auth

/**
 * An interface representing a [User], which may be used to perform some basic authentication
 * queries.
 */
interface User {

  /** The unique identifier for this [User]. */
  val uid: String

  /** The email address for this user, if the user performed authentication with their email. */
  val email: String?
}
