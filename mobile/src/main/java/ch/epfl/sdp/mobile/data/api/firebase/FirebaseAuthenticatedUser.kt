package ch.epfl.sdp.mobile.data.api.firebase

import ch.epfl.sdp.mobile.data.api.AuthenticationApi
import ch.epfl.sdp.mobile.data.api.AuthenticationApi.User.Authenticated.UpdateScope
import ch.epfl.sdp.mobile.data.api.ProfileColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticatedUser(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
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

  private data class UpdateScopeImpl(
      override var emoji: String? = null,
      override var backgroundColor: ProfileColor? = null,
      override var name: String? = null
  ) : UpdateScope

  override suspend fun update(block: UpdateScope.() -> Unit): Boolean {
    val updated = mutableMapOf<String, Any?>()
    val scope = UpdateScopeImpl()
    block(scope)
    if (scope.emoji != null) {
      updated["emoji"] = scope.emoji
    }
    if (scope.backgroundColor != null) {
      updated["backgroundColor"] = "pink" // TODO : Support other colors.
    }
    if (scope.name != null) {
      updated["name"] = scope.name
    }
    return try {
      firestore.collection("users").document(user.uid).set(updated, SetOptions.merge()).await()
      true
    } catch (exception: Throwable) {
      false
    }
  }

  override suspend fun signOut() {
    auth.signOut()
  }

  override fun toString(): String {
    return super.toString() + " username : $name"
  }
}
