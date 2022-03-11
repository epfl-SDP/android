package ch.epfl.sdp.mobile.application.firebase

import ch.epfl.sdp.mobile.application.AuthenticationFacade
import ch.epfl.sdp.mobile.application.AuthenticationFacade.User.Authenticated.UpdateScope
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthenticatedUser(
    private val auth: FirebaseAuth,
    private val firestore: Store,
    private val user: FirebaseUser,
    document: FirebaseProfileDocument?,
) : AuthenticationFacade.User.Authenticated {

  override val emoji: String = document?.emoji ?: "😎"

  override val backgroundColor: ProfileColor =
      when (document?.backgroundColor) {
        // TODO : Add more colors.
        "pink" -> ProfileColor.Pink
        else -> ProfileColor.Pink
      }

  override val name: String = document?.name ?: ""

  override val email: String = user.email ?: ""

  private data class UpdateScopeAdapter(private val scope: DocumentEditScope) : UpdateScope {

    override fun emoji(emoji: String) {
      scope["emoji"] = emoji
    }

    override fun backgroundColor(color: ProfileColor?) {
      scope["backgroundColor"] =
          when (color) {
            ProfileColor.Pink -> "pink"
            else -> null
          }
    }

    override fun name(name: String) {
      scope["name"] = name
    }
  }

  override suspend fun update(block: UpdateScope.() -> Unit): Boolean {
    return try {
      firestore.collection("users").document(user.uid).set { UpdateScopeAdapter(this).also(block) }
      true
    } catch (exception: Throwable) {
      false
    }
  }

  override suspend fun signOut() {
    auth.signOut()
  }

  override val following: Flow<List<AuthenticationFacade.Profile>> =
      firestore.collection("users").asFlow<FirebaseProfileDocument>().map {
        it.mapNotNull { doc -> doc?.toProfile() }
      }
}

// TODO : Combine method to re-use some bits of FirebaseAuthenticatedUser
private fun FirebaseProfileDocument.toProfile(): AuthenticationFacade.Profile {
  return object : AuthenticationFacade.Profile {
    override val emoji: String = this@toProfile.emoji ?: "😎"
    override val name: String = this@toProfile.name ?: ""
    override val backgroundColor: ProfileColor =
        ProfileColor.Pink // TODO: Fix these proliferating colors
  }
}
