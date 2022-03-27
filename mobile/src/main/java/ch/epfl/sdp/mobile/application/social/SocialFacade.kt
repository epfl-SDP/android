package ch.epfl.sdp.mobile.application.social

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticationUser
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * An interface which represents all the endpoints and available features for social interaction for
 * a user of the Pawnies application.
 *
 * @param auth the [Auth] instance which will be used to handle authentication.
 * @param store the [Store] which is used to manage documents.
 */
class SocialFacade(private val auth: Auth, private val store: Store) {

  /**
   * Searches user by exact match on name
   *
   * @param text search criteria.
   * @param user the [AuthenticationUser] that is performing the search.
   */
  fun search(
      text: String,
      user: AuthenticationUser = NotAuthenticatedUser,
  ): Flow<List<Profile>> {
    return store.collection("users").whereEquals("name", text).asFlow<ProfileDocument>().map {
      it.mapNotNull { doc -> doc?.toProfile(user) }
    }
  }

  /**
   * Returns a [Flow] of the [Profile] by the given name
   *
   * @param uid the unique identifiers of the profile.
   * @param user the [AuthenticationUser] that is performing the get.
   */
  fun profile(
      uid: String,
      user: AuthenticationUser = NotAuthenticatedUser,
  ): Flow<Profile?> {
    return store.collection("users").document(uid).asFlow<ProfileDocument>().map { doc ->
      doc?.toProfile(user)
    }
  }
}
