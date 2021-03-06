package ch.epfl.sdp.mobile.application

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationUser
import com.google.firebase.firestore.DocumentId

/**
 * A document which represents the profile of a user. All the profile documents are stored in the
 * `/users/` collection.
 *
 * @property uid the unique identifier for this profile.
 * @property name the human-readable name associated to this profile.
 * @property emoji the emoji associated with this profile.
 * @property backgroundColor the hex color code for this profile.
 * @property followers a list of unique identifiers of the users who follow this profile.
 * @property solvedPuzzles a list of unique puzzle ids representing puzzles solved by the user
 */
data class ProfileDocument(
    @DocumentId val uid: String? = null,
    val name: String? = null,
    val emoji: String? = null,
    val backgroundColor: String? = null,
    val followers: List<String>? = null,
    val solvedPuzzles: List<String>? = null,
) {
  companion object {

    /** The name of the collection. */
    const val Collection = "users"

    /** The name field of this document. */
    const val Name = "name"

    /** The emoji field of this document. */
    const val Emoji = "emoji"

    /** The backgroundColor field of this document. */
    const val BackgroundColor = "backgroundColor"

    /** The followers field of this document. */
    const val Followers = "followers"

    /** The solvedPuzzles field of this document. */
    const val SolvedPuzzles = "solvedPuzzles"
  }
}

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
 * @property uid the unique identifier of the document.
 * @property moves the list of moves played during the match in long algebraic notation
 * @property whiteId the UID of the white player
 * @property blackId The UID of the black player
 * @property lastUpdatedAt when the last update of the document happened
 * @property metadata additional information to a Chess document
 * @property tournamentId the identifier of the linked tournament, if there's any.
 * @property poolId the identifier of the linked pool, if there's any.
 * @property roundDepth the depth of the round within the pool.
 */
data class ChessDocument(
    @DocumentId val uid: String? = null,
    val moves: List<String>? = null,
    val whiteId: String? = null,
    val blackId: String? = null,
    val lastUpdatedAt: Long? = null,
    val metadata: ChessMetadata? = null,

    // Tournament information.
    val tournamentId: String? = null,
    val poolId: String? = null,
    val roundDepth: Int? = null,
) {
  companion object {

    /** The name of the collection. */
    const val Collection = "games"

    /** The moves field of this document. */
    const val Moves = "moves"

    /** The whiteId colorField. */
    const val WhiteId = "whiteId"

    /** The blackId colorField. */
    const val BlackId = "blackId"

    /** The lastUpdatedAt field of this document. */
    const val LastUpdatedAt = "lastUpdatedAt"

    /** The metadata field of this document. */
    const val Metadata = "metadata"

    /** The status field of this document. */
    const val Status = "status"

    /** The blackName field of this document. */
    const val BlackName = "blackName"

    /** The whiteName field of this document. */
    const val WhiteName = "whiteName"

    /** The tournamentId field of this document. */
    const val TournamentId = "tournamentId"

    /** The poolId field of this document. */
    const val PoolId = "poolId"

    /** The roundDepth field of this document. */
    const val RoundDepth = "roundDepth"
  }
}

/**
 * Additional information to a ChessDocument. This can speed up some queries.
 *
 * @property status this is the status of the game, if its already won, undecided or stalemate
 * @property blackName name of the black player
 * @property whiteName name of the white player
 */
data class ChessMetadata(
    val status: String? = null,
    val blackName: String? = null,
    val whiteName: String? = null
) {
  companion object {

    /** Indicates that the white player won. */
    const val WhiteWon = "whiteWon"

    /** Indicates that thee black player won. */
    const val BlackWon = "blackWon"

    /** Indicates that there was a stalemate. */
    const val Stalemate = "stalemate"
  }
}

/**
 * A document which represents a tournament of chess between many users. All the tournament
 * documents are stored in the `/tournaments/` collection.
 *
 * @property uid the unique identifier for this tournament.
 * @property adminId the unique identifier of the user administrating the tournament.
 * @property name the name of the tournament.
 * @property maxPlayers the maximum number of players than can join this tournament.
 * @property creationTimeEpochMillis the time of creation of the tournament.
 * @property bestOf the number of "best-of" rounds for the pool phase and the direct elimination
 * phase.
 * @property poolSize the target size of each pool. The number of pools derives from this number and
 * the total number of players.
 * @property eliminationRounds the number of direct elimination rounds. 1 for just a final, 2 for
 * semi-finals, 3 for quarter-finals, etc...
 * @property playerIds the [List] of unique identifier of users that have joined the tournament.
 * @property stage the current stage of the tournament.
 */
data class TournamentDocument(
    @DocumentId val uid: String? = null,
    val adminId: String? = null,
    val name: String? = null,
    val maxPlayers: Int? = null,
    val creationTimeEpochMillis: Long? = null,
    val bestOf: Int? = null,
    val poolSize: Int? = null,
    val eliminationRounds: Int? = null,
    val playerIds: List<String>? = null,
    val stage: String? = null,
) {
  companion object {

    /** The name of the collection. */
    const val Collection = "tournaments"

    /** The field with the tournament name. */
    const val Name = "name"

    /** The field with the tournament participants. */
    const val Participants = "playerIds"

    /** Indicates the [TournamentDocument.stage] of the pools. */
    const val StagePools = "pools"

    /** The creationTimeEpochMillis field of this document. */
    const val CreationTimeEpochMillis = "creationTimeEpochMillis"

    /**
     * Indicates the depth of the last created elimination round.
     *
     * @param depth the depth of the round.
     */
    fun stageDirectElimination(depth: Int): String {
      return "$depth"
    }
  }
}

/**
 * A document which represents a pool in a tournament of chess. All the pool documents are stored
 * inside their corresponding [TournamentDocument], in `tournaments/tournamentId/`.
 *
 * @property uid the unique identifier for this pool.
 * @property name the name of this pool.
 * @property tournamentId the unique identifier of the tournament in which the pool takes place.
 * @property minOpponentsForAnyPool the minimum number of opponents played by each player.
 * @property remainingBestOfCount the number of remaining matches to play in the current round.
 * @property tournamentBestOf the number of rounds to play between each opponent pair.
 * @property tournamentAdminId the identifier of the tournament administrator.
 * @property playerIds the [List] of unique identifier of users that have been placed in this pool.
 * @property playerNames the [List] of player names.
 */
data class PoolDocument(
    @DocumentId val uid: String? = null,
    val name: String? = null,
    val tournamentId: String? = null,
    val minOpponentsForAnyPool: Int? = null,
    val remainingBestOfCount: Int? = null,
    val tournamentBestOf: Int? = null,
    val tournamentAdminId: String? = null,
    val playerIds: List<String>? = null,
    val playerNames: List<String>? = null,
) {
  companion object {

    /** The name of the collection. */
    const val Collection = "pools"

    /** The tournamentId field of this document. */
    const val TournamentId = "tournamentId"

    /** The name field of this document. */
    const val Name = "name"
  }
}
