package ch.epfl.sdp.mobile.application.authentication

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileColor
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthenticatedUser(
    private val auth: Auth,
    private val firestore: Store,
    private val user: User,
    document: ProfileDocument?,
) : AuthenticationUser, Profile by document.toProfile() {

  override val emoji: String = document?.emoji ?: "😎"

  override val backgroundColor: ProfileColor =
      when (document?.backgroundColor) {
        // TODO : Add more colors.
        "pink" -> ProfileColor.Pink
        else -> ProfileColor.Pink
      }

  override val name: String = document?.name ?: ""

  val email: String = user.email ?: ""

  class UpdateScope(private val scope: DocumentEditScope) {

    fun emoji(emoji: String): Unit = scope.set("emoji", emoji)

    // TODO : De-uglify this.
    fun backgroundColor(color: ProfileColor?) {
      scope["backgroundColor"] =
          when (color) {
            ProfileColor.Pink -> "pink"
            else -> null
          }
    }

    fun name(name: String) = scope.set("name", name)
  }

  suspend fun update(block: UpdateScope.() -> Unit): Boolean {
    return try {
      firestore.collection("users").document(user.uid).set { UpdateScope(this).also(block) }
      true
    } catch (exception: Throwable) {
      false
    }
  }

  suspend fun signOut() {
    auth.signOut()
  }

  val following: Flow<List<Profile>> =
      firestore.collection("users").asFlow<ProfileDocument>().map {
        it.mapNotNull { doc -> doc?.toProfile() }
      }
}

// TODO : Combine method to re-use some bits of FirebaseAuthenticatedUser
private fun ProfileDocument?.toProfile(): Profile {
  return object : Profile {
    override val emoji: String = this@toProfile?.emoji ?: "😎"
    override val name: String = this@toProfile?.name ?: ""
    override val backgroundColor: ProfileColor =
        ProfileColor.Pink // TODO: Fix these proliferating colors
  }
}
