package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.notation.deserialize
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.MatchResult
import ch.epfl.sdp.mobile.ui.social.Tie
import java.util.*
import kotlinx.coroutines.flow.*

/**
 * An interface which represents all the endpoints and available features for online chess
 * interactions for a user of the Pawnies application.
 *
 * @param auth the [Auth] instance which will be used to handle authentication.
 * @param store the [Store] which is used to manage documents.
 */
class ChessFacade(private val auth: Auth, private val store: Store) {

  /**
   * Creates a [Match] between two [Profile]s and stores it in the [Store]
   *
   * @param white The [Profile] of the player that will play white
   * @param black The [Profile] of the player that will play black
   *
   * @return The created [Match] before storing it in the [Store] (without the GameId)
   */
  suspend fun createMatch(white: Profile, black: Profile): Match {
    val id = UUID.randomUUID().toString()
    val match = StoreMatch(id, store)
    store
        .collection("games")
        .document(id)
        .set(
            ChessDocument(
                moves = emptyList(),
                whiteId = white.uid,
                blackId = black.uid,
            ),
        )

    return match
  }

  /**
   * Returns the [Match] associated to the given identifier.
   *
   * @param id the unique identifier for this [Match].
   */
  fun match(id: String): Match {
    return StoreMatch(id, store)
  }

  /**
   * Fetches a [Flow] of [List] of [Match]s that a certain [Profile] has going on with any other
   * player (or even himself)
   *
   * @param profile The [Profile] whose [Match]s will be fetched
   *
   * @return The [Flow] of [List] of [Match]s for the [Profile]
   */
  fun matches(profile: Profile): Flow<List<Match>> {
    val gamesAsWhite = getMatchesForPlayer(colorField = "whiteId", playerId = profile.uid)
    val gamesAsBlack = getMatchesForPlayer(colorField = "blackId", playerId = profile.uid)

    return combine(gamesAsWhite, gamesAsBlack) { (a, b) -> a + b }
  }

  private fun getMatchesForPlayer(colorField: String, playerId: String): Flow<List<Match>> {
    return store.collection("games").whereEquals(colorField, playerId).asMatchListFlow().onStart {
      emit(emptyList())
    }
  }

  private fun Query.asMatchListFlow(): Flow<List<Match>> {
    return this.asFlow<ChessDocument>().map {
      it.filterNotNull().mapNotNull(ChessDocument::uid).map { uid -> StoreMatch(uid, store) }
    }
  }

  /**
   * Determines the [MatchResult] of a given [Match]
   *
   * @param match the [Match] to determine its [MatchResult]
   */
  suspend fun determineMatchOutcome(match: Match): MatchResult? {
    val game = match.game.filterNotNull().first()
    when (game.nextStep) {
      is NextStep.Checkmate -> MatchResult.Reason.CHECKMATE
      is NextStep.Stalemate -> Tie
      else -> Tie
    }
    return Tie
  }

  /**
   * Returns a [Flow] of a list of [ChessMatch] of the matches played by the given profile.
   *
   * @param profile the profile we want to know their played [ChessMatch]es
   */
  fun chessMatches(profile: Profile): Flow<List<ChessMatch>> {
    val matches = matches(profile)
    return matches.map { list -> list.mapNotNull { match -> match.toChessMatch(profile.uid) } }
  }

  /**
   * Transforms a given [Match] to a [ChessMatch].
   *
   * @param currentUid the uid of the current user.
   */
  suspend fun Match.toChessMatch(
      currentUid: String,
  ): ChessMatch? {
    val blackID = this.black.filterNotNull().first().uid
    val whiteID = this.white.filterNotNull().first().uid
    val adversary = if (blackID == currentUid) whiteID else blackID
    val result = determineMatchOutcome(this)
    val game = this.game.first()
    val moveNum = game.serialize().size
    return result?.let { ChessMatch(adversary, result, moveNum) }
  }
}

private data class StoreMatch(
    override val id: String,
    private val store: Store,
) : Match {

  fun profile(
      uid: String,
  ): Flow<Profile?> {

    return store.collection("users").document(uid).asFlow<ProfileDocument>().map { doc ->
      doc?.toProfile(NotAuthenticatedUser)
    }
  }

  private val documentFlow = store.collection("games").document(id).asFlow<ChessDocument>()

  override val game = documentFlow.map { it?.moves ?: emptyList() }.map { it.deserialize() }

  override val white =
      documentFlow.map { it?.whiteId }.flatMapLatest {
        it?.let(this@StoreMatch::profile) ?: flowOf(null)
      }
  override val black =
      documentFlow.map { it?.blackId }.flatMapLatest {
        it?.let(this@StoreMatch::profile) ?: flowOf(null)
      }

  override suspend fun update(game: Game) {
    store.collection("games").document(id).update { this["moves"] = game.serialize() }
  }
}
