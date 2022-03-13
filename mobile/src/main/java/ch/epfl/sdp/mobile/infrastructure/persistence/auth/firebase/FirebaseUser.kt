package ch.epfl.sdp.mobile.infrastructure.persistence.auth.firebase

import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import com.google.firebase.auth.FirebaseUser as ActualFirebaseUser

/**
 * An implementation of [User] which makes use of the [ActualFirebaseUser] API and adapts it.
 *
 * @param actual the [ActualFirebaseUser].
 */
class FirebaseUser(private val actual: ActualFirebaseUser) : User {

  override val uid: String
    get() = actual.uid

  override val email: String?
    get() = actual.email
}
