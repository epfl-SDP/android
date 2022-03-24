package ch.epfl.sdp.mobile.application.chess.online

import ch.epfl.sdp.mobile.application.*
import ch.epfl.sdp.mobile.application.chess.Game
import ch.epfl.sdp.mobile.infrastructure.persistence.auth.Auth
import ch.epfl.sdp.mobile.infrastructure.persistence.store.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class ChessFacade(private val auth: Auth, private val store: Store) {

  data class GameIdInfo(
      val gameId: String? = null,
      val whiteId: String? = null,
      val blackId: String? = null
  )

  suspend fun createGame(white: Profile, black: Profile) {
    val reference = store.collection("games").document()
    val chessDocument = Game.create().serialize(whiteId = white.uid, blackId = black.uid)

    reference.set(chessDocument)
  }

  suspend fun updateGame(id: String, game: Game) {
    val reference = store.collection("games").document(id)
    val chessDocument = game.serialize()
    reference.set(chessDocument)
  }

  private fun queryGamesForPlayer(colorField: String, playerId: String): Query {
    return store.collection("games").whereEquals(colorField, playerId)
  }

  private fun Query.asGameIds(): Flow<List<GameIdInfo>> {
    return this.asFlow<ChessDocument>().map {
      it.map { doc -> doc?.let { GameIdInfo(doc.uid, doc.whiteId, doc.blackId) } ?: GameIdInfo() }
    }
  }

  fun fetchGameIds(profile: Profile): Flow<List<GameIdInfo>> {
    val gamesAsWhite =
        queryGamesForPlayer(colorField = "whiteId", playerId = profile.uid).asGameIds()
    val gamesAsBlack =
        queryGamesForPlayer(colorField = "blackId", playerId = profile.uid).asGameIds()

    return merge(gamesAsWhite, gamesAsBlack)
  }

  fun fetchGameIdsForPlayers(player: Profile, opponent: Profile): Flow<List<GameIdInfo>> {
    val gamesAsWhite =
        queryGamesForPlayer(colorField = "whiteId", playerId = player.uid)
            .whereEquals("blackId", opponent.uid)
            .asGameIds()

    val gamesAsBlack =
        queryGamesForPlayer(colorField = "blackId", playerId = player.uid)
            .whereEquals("whiteId", opponent.uid)
            .asGameIds()

    return merge(gamesAsWhite, gamesAsBlack)
  }

  fun fetchGame(gameId: String): Flow<Game> {
    return store.collection("games").document(gameId).asGameFlow()
  }

  private fun DocumentReference.asGameFlow(): Flow<Game> {
    return asFlow<ChessDocument>().map { it?.deserialize() ?: Game.create() }
  }
}
