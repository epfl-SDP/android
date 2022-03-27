package ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.fake

/**
 * A record of the currently connected user, as well as the users which can be used to authenticate.
 */
data class FakeAuthRecord(
    val currentUser: FakeUser?,
    val users: Set<FakeUser>,
)
