package ch.epfl.sdp.mobile.data.api.firebase

import ch.epfl.sdp.mobile.backend.store.DocumentEditScope
import ch.epfl.sdp.mobile.backend.store.Store
import ch.epfl.sdp.mobile.backend.store.asFlow
import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User.Authenticated.UpdateScope
import ch.epfl.sdp.mobile.data.api.ProfileColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseAuthenticatedUser(
    private val auth: FirebaseAuth,
    private val firestore: Store,
    private val user: FirebaseUser,
    document: FirebaseProfileDocument?,
) : AuthenticationApi.User.Authenticated {

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

    override var emoji: String?
      get() = error("Not supported.")
      set(value) {
        scope["emoji"] = value
      }
    override var backgroundColor: ProfileColor?
      get() = error("Not supported.")
      set(value) {
        scope["backgroundColor"] = value
      }
    override var name: String?
      get() = error("Not supported.")
      set(value) {
        scope["name"] = value
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

  override val following: Flow<List<AuthenticationApi.Profile>> =
      firestore.collection("users").asFlow<FirebaseProfileDocument>().map {
        it.mapNotNull { doc -> doc?.toProfile() }
      }

  override fun toString(): String {
    return super.toString() + " username : $name"
  }
}

// TODO : Combine method to re-use some bits of FirebaseAuthenticatedUser
private fun FirebaseProfileDocument.toProfile(): AuthenticationApi.Profile {
  return object : AuthenticationApi.Profile {
    override val emoji: String = this@toProfile.emoji ?: "😎"
    override val name: String = this@toProfile.name ?: ""
    override val backgroundColor: ProfileColor = ProfileColor.Pink
  }
}
