package ch.epfl.sdp.mobile.application.social

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

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
   */
  fun search(text: String): Flow<List<Profile>> {
    return store.collection("users").whereEquals("name", text).asFlow<ProfileDocument>().map {
      it.mapNotNull { doc -> doc?.toProfile() }
    }
  }
  /**
   * Follows the given user
   *
   * @param followed the user to follow.
   */
  suspend fun follow(followed: Person) {
    val currentUid = auth.currentUser.firstOrNull()?.uid ?: ""
    store.collection("users").document(followed.uid).update { arrayUnion("followers", currentUid) }
  }
}
