package ch.epfl.sdp.mobile.test.infrastructure.persistence.auth

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.fake.FakeAuth

/** Creates an in-memory implementation of [Auth], which can't fail. */
fun emptyAuth(): Auth = FakeAuth()

/**
 * Builds an [Auth] using the provided [AuthBuilder].
 *
 * @param builder the builder for the auth.
 * @return the newly build fake auth.
 */
fun buildAuth(
    builder: AuthBuilder.() -> Unit,
): Auth = FakeAuth().apply(builder)

/**
 * A scope which can be used to create some users and perform some actions on an
 * [ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth] instance.
 */
interface AuthBuilder {

  /**
   * Adds a new user with the given email and password.
   *
   * @param email the email of the added user. Must be unique for this auth.
   * @param password the password which will protect authentication with the account.
   */
  fun user(email: String, password: String)
}
