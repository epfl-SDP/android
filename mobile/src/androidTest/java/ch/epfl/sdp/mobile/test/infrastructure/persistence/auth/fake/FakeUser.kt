package ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.fake

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User

/**
 * An implementation of [User] which is faked and has no behavior.
 *
 * @param uid the unique identifier for this [User].
 * @param email the email address of this [User], if available.
 * @param password the password which this user authenticates with.
 */
data class FakeUser(
    override val uid: String,
    override val email: String?,
    val password: String,
) : User
