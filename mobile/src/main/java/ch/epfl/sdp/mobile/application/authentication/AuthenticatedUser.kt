package ch.epfl.sdp.mobile.application.authentication

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import ch.epfl.sdp.mobile.infrastructure.persistence.store.DocumentEditScope
import ch.epfl.sdp.mobile.infrastructure.persistence.store.Store
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Indicates that an [AuthenticatedUser] is currently authenticated. */
class AuthenticatedUser(
    private val auth: Auth,
    private val firestore: Store,
    private val user: User,
    document: ProfileDocument?,
) : AuthenticationUser, Profile by document.toProfile() {

  /** The email of the currently logged in user. */
  val email: String = user.email ?: ""

  /**
   * A scope which wraps a [DocumentEditScope] to allow editions to a user profile.
   *
   * @param scope the wrapped [DocumentEditScope].
   */
  class UpdateScope(private val scope: DocumentEditScope) {

    /** Updates the profile emoji with [emoji]. */
    fun emoji(emoji: String): Unit = scope.set("emoji", emoji)

    /** Updates the profile color with [color]. */
    fun backgroundColor(color: Color?) = scope.set("backgroundColor", color?.hex)

    /** Updates the profile name with [name]. */
    fun name(name: String) = scope.set("name", name)
  }

  /**
   * Atomically updates the profile using the edits performed in the scope.
   *
   * @param block the [UpdateScope] in which some updates may be performed.
   * @return true iff the updates were properly applied.
   */
  suspend fun update(block: UpdateScope.() -> Unit): Boolean {
    return try {
      firestore.collection("users").document(user.uid).update { UpdateScope(this).also(block) }
      true
    } catch (exception: Throwable) {
      false
    }
  }

  /** Signs this user out of the [AuthenticationFacade]. */
  suspend fun signOut() {
    auth.signOut()
  }

  /** Returns a [Flow] of the [Profile]s which are currently followed by this user. */
  val following: Flow<List<Profile>> =
    firestore.collection("users").whereArrayContains("following", user.uid).asFlow<ProfileDocument>().map {
      it.mapNotNull { doc -> doc?.toProfile() }
      }
}

// TODO : Combine method to re-use some bits of FirebaseAuthenticatedUser
private fun ProfileDocument?.toProfile(): Profile {
  return object : Profile {
    override val emoji: String = this@toProfile?.emoji ?: "😎"
    override val name: String = this@toProfile?.name ?: ""
    override val backgroundColor: Color =
        this@toProfile?.backgroundColor?.let(::Color) ?: Color.Default
    override val uid: String = this@toProfile?.uid ?: ""
  }
}
