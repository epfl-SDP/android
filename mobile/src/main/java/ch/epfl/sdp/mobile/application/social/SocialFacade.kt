package ch.epfl.sdp.mobile.application.social

import android.util.Log
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

/**
 * An interface which represents all the endpoints and available features for social
 * interaction for a user of the Pawnies application.
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

    private fun ProfileDocument?.toProfile(): Profile {
        return object : Profile {
            override val emoji: String = this@toProfile?.emoji ?: "😎"
            override val name: String = this@toProfile?.name ?: ""
            override val backgroundColor: Profile.Color =
                this@toProfile?.backgroundColor?.let(Profile::Color) ?: Profile.Color.Default
        }
    }

}