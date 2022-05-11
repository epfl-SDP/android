package ch.epfl.sdp.mobile.application

import Tournament
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationUser
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.ui.tournaments.ContestInfo
import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @param uid the unique identifier for this profile.
 * @param name the human-readable name associated to this profile.
 * @param emoji the emoji associated with this profile.
 * @param backgroundColor the hex color code for this profile.
 * @param followers a list of unique identifiers of the users who follow this profile.
 * @param solvedPuzzles a list of unique puzzle ids representing puzzles solved by the user
 */
data class ProfileDocument(
    @DocumentId val uid: String? = null,
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null,
    val followers: List<String>? = null,
    val solvedPuzzles: List<String>? = null,
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
    override val solvedPuzzles = this@toProfile?.solvedPuzzles ?: emptyList()
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
 * @param moves the list of moves played during the match in long algebraic notation
 * @param whiteId the UID of the white player
 * @param blackId The UID of the black player
 */
data class ChessDocument(
    @DocumentId val uid: String? = null,
    val moves: List<String>? = null,
    val whiteId: String? = null,
    val blackId: String? = null,
)

/**
 * A document which represents a tournament of chess between many users. All the tournament
 * documents are stored in the `/tournaments/` collection.
 *
 * @param uid the unique identifier for this tournament.
 * @param adminId the unique identifier of the user administrating the tournament.
 * @param name the name of the tournament.
 * @param maxPlayers the maximum number of players than can join this tournament.
 * @param bestOf the number of "best-of" rounds for the pool phase and the direct elimination phase.
 * @param poolSize the target size of each pool. The number of pools derives from this number and
 * the total number of players.
 * @param eliminationRounds the number of direct elimination rounds. 1 for just a final, 2 for
 * semi-finals, 3 for quarter-finals, etc...
 * @param playerIds the [List] of unique identifier of users that have joined the tournament.
 */
data class TournamentDocument(
    @DocumentId val uid: String? = null,
    val adminId: String? = null,
    val name: String? = null,
    val maxPlayers: Int? = null,
    val bestOf: Int? = null,
    val poolSize: Int? = null,
    val eliminationRounds: Int? = null,
    val playerIds: List<String>? = null,
)

/** Transforms a [TournamentDocument] to a [Tournament]. */
fun TournamentDocument?.toTournament(): Tournament {
  return object : Tournament {
    override val uid: String = this@toTournament?.uid ?: ""
    override val name: String = this@toTournament?.name ?: ""
    override val adminId: String = this@toTournament?.adminId ?: ""
    // TODO : Add Creation Date and Status parameters.
    override val maxPlayers: Int = this@toTournament?.maxPlayers ?: 0
    override val bestOf: Int = this@toTournament?.bestOf ?: 1
    override val poolSize: Int = this@toTournament?.poolSize ?: 0
    override val eliminationRounds: Int = this@toTournament?.eliminationRounds ?: 0
    override val playerIds: List<String> = this@toTournament?.playerIds ?: emptyList()
  }
}

/**
 * A document which represents a pool in a tournament of chess. All the pool documents are stored
 * inside their corresponding [TournamentDocument], in `tournaments/tournamentId/`.
 *
 * @param uid the unique identifier for this pool.
 * @param tournamentId the unique identifier of the tournament in which the pool takes place.
 * @param currentRound the current round number for the pool.
 * @param playerIds the [List] of unique identifier of users that have been placed in this pool.
 */
data class PoolDocument(
    @DocumentId val uid: String? = null,
    val tournamentId: String? = null,
    val currentRound: Int? = null,
    val playerIds: List<String>? = null,
)
