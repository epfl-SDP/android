package ch.epfl.sdp.mobile.application.authentication

import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.User
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import ch.epfl.sdp.mobile.application.chess.Puzzle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Indicates that an [AuthenticatedUser] is currently authenticated. */
class AuthenticatedUser(
    private val auth: Auth,
    private val firestore: Store,
    private val user: User,
    document: ProfileDocument?,
) : AuthenticationUser, Profile by document.toProfile(user.uid) {

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

    fun solvedPuzzles(puzzle: Puzzle) = scope.arrayUnion("solvedPuzzles", puzzle.uid)
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

  /**
   * Follows the given [Profile] by updating its list of followers with the uid of the current user.
   *
   * @param followed the [Profile] to follow.
   */
  suspend fun follow(followed: Profile) {
    firestore.collection("users").document(followed.uid).update {
      arrayUnion("followers", user.uid)
    }
  }

  /**
   * Unfollows the given [Profile] by removing the uid of the current user from its list of
   * followers.
   *
   * @param unfollowed the [Profile] to unfollow.
   */
  suspend fun unfollow(unfollowed: Profile) {
    firestore.collection("users").document(unfollowed.uid).update {
      arrayRemove("followers", user.uid)
    }
  }

  /** Signs this user out of the [AuthenticationFacade]. */
  suspend fun signOut() {
    auth.signOut()
  }

  /**
   * Returns a [Flow] of the [Profile]s which are currently followed by this user by going through
   * all users' list of followers.
   */
  val following: Flow<List<Profile>> =
      firestore
          .collection("users")
          .whereArrayContains("followers", user.uid)
          .orderBy("name")
          .asFlow<ProfileDocument>()
          .map { it.mapNotNull { doc -> doc?.toProfile(this) } }
}
