package ch.epfl.sdp.mobile.application

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.ui.game.ChessBoardCells
import com.google.firebase.firestore.DocumentId

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @param uid the unique identifier for this profile.
 * @param name the human-readable name associated to this profile.
 * @param emoji the emoji associated with this profile.
 * @param backgroundColor the hex color code for this profile.
 * @param followers a list of unique identifiers of the users who follow this profile.
 */
data class ProfileDocument(
    @DocumentId val uid: String? = null,
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null,
    val followers: List<String>? = null,
)

/**
 * Transforms a given unique identifier to a [Profile].
 *
 * @param currentUserUid the unique identifier [String] of the [ProfileDocument] to transform.
 */
fun ProfileDocument?.toProfile(currentUserUid: String?): Profile {
  return object : Profile {
    override val emoji: String = this@toProfile?.emoji ?: "😎"
    override val name: String = this@toProfile?.name ?: ""
    override val backgroundColor: Profile.Color =
        this@toProfile?.backgroundColor?.let(Profile::Color) ?: Profile.Color.Default
    override val uid: String = this@toProfile?.uid ?: ""
    override val followed: Boolean = currentUserUid in (this@toProfile?.followers ?: emptyList())
  }
}

/**
 * Transforms a given [AuthenticationUser] to a [Profile].
 *
 * @param currentUser the [AuthenticationUser] to transform.
 */
fun ProfileDocument?.toProfile(currentUser: AuthenticationUser): Profile {
  return toProfile((currentUser as? AuthenticatedUser)?.uid)
}

/**
 * A document which represents a game of chess between two users. All the game documents are stored
 * in the `/games/` collection.
 *
 * @param moves
 * @param whiteId
 * @param blackId
 */
data class ChessDocument(
    @DocumentId val uid: String? = null,
    val moves: List<String>? = null,
    val whiteId: String? = null,
    val blackId: String? = null,
)