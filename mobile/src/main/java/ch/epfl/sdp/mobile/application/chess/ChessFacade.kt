package ch.epfl.sdp.mobile.application.chess

import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.NotAuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.engine.Game
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.application.chess.notation.mapToGame
import ch.epfl.sdp.mobile.application.toProfile
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
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
   * Creates a "local" [Match] for the [AuthenticatedUser] and stores it in the [Store]
   *
   * @param user The [AuthenticatedUser] that wants to create the [Match]
   *
   * @return The created [Match] before storing it in the [Store]
   */
  suspend fun createLocalMatch(user: AuthenticatedUser): Match {
    val document = store.collection("games").document()
    document.set(ChessDocument(whiteId = user.uid, blackId = user.uid))
    return StoreMatch(document.id, store)
  }

  /**
   * Creates a [Match] between two [Profile]s and stores it in the [Store]
   *
   * @param white The [Profile] of the player that will play white
   * @param black The [Profile] of the player that will play black
   *
   * @return The created [Match] before storing it in the [Store]
   */
  suspend fun createMatch(white: Profile, black: Profile): Match {
    val document = store.collection("games").document()
    document.set(ChessDocument(whiteId = white.uid, blackId = black.uid))
    return StoreMatch(document.id, store)
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

    return combine(gamesAsWhite, gamesAsBlack) { (a, b) -> a.union(b).sortedBy { it.id } }
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

  override val game = documentFlow.map { it?.moves ?: emptyList() }.mapToGame()

  override val white =
      documentFlow.map { it?.whiteId }.flatMapLatest {
        it?.let(this@StoreMatch::profile) ?: flowOf(null)
      }
  override val black =
      documentFlow.map { it?.blackId }.flatMapLatest {
        it?.let(this@StoreMatch::profile) ?: flowOf(null)
      }

  override suspend fun update(game: Game) {
    store.collection("games").document(id).update { this["moves"] = game.toAlgebraicNotation() }
  }
}
