package ch.epfl.sdp.mobile.application.social

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

  fun search(text: String): Flow<List<Profile>> {
    return store.collection("users").whereEquals("name", text).asFlow<ProfileDocument>().map {
      it.mapNotNull { doc -> doc?.toProfile() }
    }
  }

  suspend fun follow(followed: Person, scope: DocumentEditScope) {
    store.collection("users").document(auth.currentUser.firstOrNull()?.uid ?: "").update { scope.arrayUnion("following", followed.uid) }
  }

  private fun ProfileDocument?.toProfile(): Profile {
    return object : Profile {
      override val emoji: String = this@toProfile?.emoji ?: "😎"
      override val name: String = this@toProfile?.name ?: ""
      override val backgroundColor: Profile.Color =
          this@toProfile?.backgroundColor?.let(Profile::Color) ?: Profile.Color.Default
      override val uid: String = this@toProfile?.uid ?: ""
    }
  }
}
